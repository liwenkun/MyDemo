package me.liwenkun.demo.demoframework

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.liwenkun.demo.App.Companion.get
import me.liwenkun.demo.BaseActivity
import me.liwenkun.demo.R
import me.liwenkun.demo.demoframework.DemoBook.DemoItem
import thereisnospon.codeview.CodeView
import thereisnospon.codeview.CodeViewTheme

open class DemoBaseActivity : BaseActivity(), Logger {
    private lateinit var logView: LogView
    private lateinit var contentView: ViewGroup
    private lateinit var codeView: CodeView
    private val demoItemDao = get().appDatabase.demoItemDao()
    private lateinit var demoId: String

    private interface MenuItemState {
        @get:StringRes
        val desc: Int
        fun onClick(menuItem: MenuItem)
    }

    private val closeState: MenuItemState = object : MenuItemState {
        override val desc = R.string.open_log_view
        override fun onClick(menuItem: MenuItem) {
            logView.visibility = View.VISIBLE
            menuItem.setTitle(R.string.close_log_view)
            currentLogState = openState
        }
    }
    private val openState: MenuItemState = object : MenuItemState {
        override val desc = R.string.close_log_view
        override fun onClick(menuItem: MenuItem) {
            logView.visibility = View.INVISIBLE
            menuItem.setTitle(R.string.open_log_view)
            currentLogState = closeState
        }
    }
    private val starState: MenuItemState = object : MenuItemState {
        override val desc = R.string.has_star
        override fun onClick(menuItem: MenuItem) {
            menuItem.isEnabled = false
            CoroutineScope(Dispatchers.IO).launch {
                demoItemDao.star(demoId, false)
            }
        }
    }
    private val unStarState: MenuItemState = object : MenuItemState {
        override val desc = R.string.star
        override fun onClick(menuItem: MenuItem) {
            menuItem.isEnabled = false
            CoroutineScope(Dispatchers.IO).launch {
                demoItemDao.star(demoId, true)
            }
        }
    }
    private var currentLogState = closeState
    private var currentStarState = unStarState
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_demo_base_activity)
        demoId = intent.getStringExtra(EXTRA_DEMO_ID).toString()
        contentView = findViewById(R.id.content)
        initCodeView()
        logView = findViewById(R.id.log_view)
        if (showLogOnStart()) {
            logView.visibility = View.VISIBLE
        }
        demoItemDao.get(demoId).observe(this) { demoItem: DemoItem -> title = demoItem.name }
    }

    protected open fun showLogOnStart(): Boolean {
        return false
    }

    private fun initCodeView() {
        codeView = findViewById<CodeView>(R.id.code_view)
            .setTheme(CodeViewTheme.ATELIER_FOREST_DARK)
            .fillColor()
            .setEncode("utf-8")
    }

    fun setSourceCode(sourceCode: String?) {
        codeView.showCode(sourceCode)
        codeView.visibility = View.VISIBLE
    }

    override fun setContentView(view: View) {
        setContentView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        contentView.removeAllViews()
        contentView.addView(view, params)
    }

    override fun setContentView(layoutResID: Int) {
        contentView.removeAllViews()
        LayoutInflater.from(this).inflate(layoutResID, contentView, true)
    }

    override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
        contentView.addView(view, params)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_demo, menu)
        val menuItem = menu.findItem(R.id.menu_demo_star)
        demoItemDao.get(demoId).observe(this) { demoItem: DemoItem ->
            if (demoItem.isStarred) {
                currentStarState = starState
                menuItem.setTitle(currentStarState.desc)
            } else {
                currentStarState = unStarState
                menuItem.setTitle(currentStarState.desc)
            }
            menuItem.isEnabled = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_demo_open_log) {
            currentLogState.onClick(item)
            return true
        } else if (item.itemId == R.id.menu_demo_star) {
            currentStarState.onClick(item)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun deleteAllLogs() {
        logView.deleteAllLogs()
    }

    override fun log(tag: String?, message: String?, color: Int, promptChar: String?) {
        logView.print(tag, message, color, promptChar)
    }

    companion object {
        const val EXTRA_DEMO_ID = "demo_item"
        @JvmStatic
        fun getRequiredExtras(demoId: String?): Bundle {
            val bundle = Bundle()
            bundle.putString(EXTRA_DEMO_ID, demoId)
            return bundle
        }

        fun show(context: Activity, demoItem: DemoItem) {
            val showDemo = Intent(context, demoItem.demoPage)
            showDemo.putExtras(getRequiredExtras(demoItem.path))
            context.startActivity(showDemo)
        }
    }
}