package me.liwenkun.demo

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                val windowInsetsController =
//            WindowCompat.getInsetsController(window, window.decorView)
//        // Configure the behavior of the hidden system bars.
//        windowInsetsController.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

//        enableEdgeToEdge(statusBarStyle = SystemBarStyle.auto(color(R.color.primary_dark)
//        , Color.BLACK),
//            navigationBarStyle = SystemBarStyle.auto(color(R.color.primary_dark), Color.BLACK))
////
////        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
////            window.statusBarColor = resources.getColor(R.color.primary_dark)
////
////        }
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
//            val systemBars =
//                insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
//            v.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
////            window.addContentView(View(this).apply {
////                elevation = -1f
////                setBackgroundColor(ResourcesCompat.getColor(resources, R.color.primary_dark, theme))
////            }, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, systemBars.top))
//            insets
//        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onResume() {
        super.onResume()

    }
}