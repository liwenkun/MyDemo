package me.liwenkun.demo.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.liwenkun.demo.R
import me.liwenkun.demo.demoframework.DemoBaseActivity
import me.liwenkun.demo.libannotation.Demo
import me.liwenkun.demo.utils.post
import me.liwenkun.demo.utils.px
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

@Demo(title = "Fragment 事务和生命周期的关系")
class FragmentActivity : DemoBaseActivity() {

    private var currentSelectedOp: TransactionOp? = null
    private val transactionHelper = TransactionHelper(supportFragmentManager)

    private val optionsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> =
        object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val textView = AppCompatTextView(this@FragmentActivity)
                textView.setPadding(px(10), px(10), px(10), px(10))
                return object : RecyclerView.ViewHolder(textView) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as TextView).text = TransactionOp.values()[position].name
                holder.itemView.setOnClickListener { v: View? ->
                    currentSelectedOp = TransactionOp.values()[position]
                    notifyItemRangeChanged(0, TransactionOp.values().size)
                }
                if (currentSelectedOp === TransactionOp.values()[position]) {
                    holder.itemView.setBackgroundColor(Color.RED)
                } else {
                    holder.itemView.setBackgroundColor(Color.GRAY)
                }
            }

            override fun getItemCount(): Int {
                return TransactionOp.values().size
            }
        }

    private val tagsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> =
        object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val textView = AppCompatTextView(this@FragmentActivity)
                textView.setBackgroundColor(Color.LTGRAY)
                val layoutParams = MarginLayoutParams(
                    parent.width / (TAGS.size + 1),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                layoutParams.rightMargin = px(5)
                textView.layoutParams = layoutParams
                textView.setPadding(px(10), px(10), px(10), px(10))
                return object : RecyclerView.ViewHolder(textView) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as TextView).text = TAGS[position]
                holder.itemView.setOnClickListener { v: View? ->
                    if (currentSelectedOp != null) {
                        transactionHelper.addTransactionOp(currentSelectedOp!!, TAGS[position])
                        currentSelectedOp = null
                        optionsAdapter.notifyItemRangeChanged(0, TransactionOp.values().size)
                    } else {
                        Toast.makeText(this@FragmentActivity, "请先选择操作", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun getItemCount(): Int {
                return TAGS.size
            }
        }


    private val opsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> =
        object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val textView = AppCompatTextView(this@FragmentActivity)
                textView.setBackgroundColor(-0x100)
                val layoutParams = MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                layoutParams.rightMargin = px(5)
                textView.layoutParams = layoutParams
                textView.setPadding(px(10), px(10), px(10), px(10))
                return object : RecyclerView.ViewHolder(textView) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val transactionOpActions = transactionHelper.transactionOp.value
                if (transactionOpActions != null) {
                    (holder.itemView as TextView).text =
                        transactionOpActions[position].actionDesc
                }
            }

            override fun getItemCount(): Int {
                val transactionOpActions = transactionHelper.transactionOp.value
                return transactionOpActions?.size ?: 0
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val opOptions = findViewById<RecyclerView>(R.id.rv_options)
        opOptions.itemAnimator = null
        opOptions.adapter = optionsAdapter
        opOptions.layoutManager =
            LinearLayoutManager(this@FragmentActivity, LinearLayoutManager.HORIZONTAL, false)

        val tags = findViewById<RecyclerView>(R.id.rv_tags)
//        tags.adapter = tagsAdapter
        post {
            tags.adapter = tagsAdapter
        }
        tags.layoutManager =
            LinearLayoutManager(this@FragmentActivity, LinearLayoutManager.HORIZONTAL, false)

        val ops = findViewById<RecyclerView>(R.id.rv_ops)
        ops.adapter = opsAdapter
        ops.layoutManager =
            LinearLayoutManager(this@FragmentActivity, LinearLayoutManager.HORIZONTAL, false)

        transactionHelper.transactionOp.observe(this) { opsAdapter.notifyDataSetChanged() }

        val cbAddToBackStack = findViewById<CheckBox>(R.id.cb_add_to_backstack)
        findViewById<View>(R.id.btn_commit).setOnClickListener { v: View ->
            if (transactionHelper.size() > 0) {
                transactionHelper.commit(cbAddToBackStack.isChecked)
                Handler(mainLooper).post { logFragmentInfo() }
            } else {
                Toast.makeText(this@FragmentActivity, "操作队列为空", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<View>(R.id.btn_clear_op).setOnClickListener { transactionHelper.cleanTransactionOp() }
    }

    override fun onPostResume() {
        val tags = findViewById<RecyclerView>(R.id.rv_tags)
        super.onPostResume()


    }

    private fun logFragmentInfo() {
        if (GET_ACTIVE_FRAGMENT != null) {
            try {
                val active = GET_ACTIVE_FRAGMENT.invoke(
                    supportFragmentManager
                ) as List<*>
                logInfo(
                    "fragmentInfo->active: " + "{count: " + active.size
                            + ", fragments: " + active + "}"
                )
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
        val added = supportFragmentManager.fragments
        logInfo(
            "fragmentInfo->added: " + "{count: " + added.size
                    + ", fragments: " + added + "}"
        )
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            Handler(mainLooper).post { logFragmentInfo() }
        }
        super.onBackPressed()
    }

    companion object {
        private val GET_ACTIVE_FRAGMENT = activeFragmentMethod
        private val TAGS = arrayOf("A", "B", "C", "D", "E", "F")
        private val activeFragmentMethod: Method?
            get() = try {
                val fragmentManagerImpl = Class.forName("androidx.fragment.app.FragmentManager")
                val method = fragmentManagerImpl.getDeclaredMethod("getActiveFragments")
                method.isAccessible = true
                method
            } catch (e: NoSuchMethodException) {
                null
            } catch (e: ClassNotFoundException) {
                null
            }
    }
}