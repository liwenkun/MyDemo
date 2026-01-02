package me.liwenkun.demo.resource

import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import me.liwenkun.demo.R
import me.liwenkun.demo.demoframework.DemoBaseActivity
import me.liwenkun.demo.libannotation.Demo
import me.liwenkun.demo.libannotation.Source

@Demo(title = "资源获取api")
class ResourceActivity : DemoBaseActivity() {

     companion object {
         @Source
         lateinit var source: String
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource)
        setSourceCode(source)

        val typedValue = TypedValue()
        //1 resourceId == color 资源 id，data == 色值
        withStyledAttributes(
            attrs = intArrayOf(androidx.appcompat.R.attr.colorPrimary)
        ) {
            getValue(0, typedValue)
            logInfo("typedValue 1-> $typedValue")
            logInfo("typedValue 1-> $typedValue")
            //2 resourceId == 0，data == color 资源 id
            theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, false)
            logInfo("typedValue 2 -> $typedValue")
            //3 等价于 1
            theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
            logInfo("typedValue 3 -> $typedValue")
        }


        //4 resourceId == 资源 id，data 值存放用来加载字符串的值（如果引用的是资源文件而不是value值，
        // 那么 resolveRefs == true 就会解析出文件，但是文件无法用 TypedValue 表示，因此只能用路径来表示，
        // 因此 TypedValue 的值就是 String 类型的了）
        withStyledAttributes(
            attrs = intArrayOf(androidx.appcompat.R.attr.selectableItemBackground)
        ) {
            getValue(0, typedValue)
            logInfo("typedValue 4 -> $typedValue")
            //5 resourceId == 0, data == drawable 资源 id
            theme.resolveAttribute(
                androidx.appcompat.R.attr.selectableItemBackground,
                typedValue,
                false
            )
            logInfo("typedValue 5 -> $typedValue")
            //6 等价于 4
            theme.resolveAttribute(
                androidx.appcompat.R.attr.selectableItemBackground,
                typedValue,
                true
            )
            logInfo("typedValue 6 -> $typedValue")
        }

        //7 resourceId == style 资源 id, data == style 资源 id
        withStyledAttributes(
            attrs = intArrayOf(com.google.android.material.R.attr.textInputStyle)
        ) {
            getValue(0, typedValue)
            logInfo("typedValue 7 -> $typedValue")
            //8 resourceId == 0, data == style 资源 id
            theme.resolveAttribute(
                com.google.android.material.R.attr.textInputStyle,
                typedValue,
                false
            )
            logInfo("typedValue 8 -> $typedValue")
            //9 等价于 7 但是 sourceResourceId 不为空
            theme.resolveAttribute(
                com.google.android.material.R.attr.textInputStyle,
                typedValue,
                true
            )
            logInfo("typedValue 9 -> $typedValue")
        }

        findViewById<TextView>(R.id.hello_world2)
            .setText(me.liwenkun.demo.library.R.string.hello_world)
        // 虽然 aapt 为主库和子库的同名资源生成了不同的资源索引，但这两个资源索引指向的资源是一样的，都是
        // 主库的那个资源，子库的资源会被覆盖。安卓同名资源的覆盖规则是：按照依赖关系，被依赖的库中的资源会被依赖的
        // 库的资源所覆盖，如果两个子库没有依赖关系，那么就看被主库依赖的顺序
        logInfo("主库 hello_world 资源 id: ${R.string.hello_world}")
        logInfo("子库1 hello_world 资源 id: ${me.liwenkun.demo.library.R.string.hello_world}")
        logInfo("子库2 hello_world 资源 id: ${me.liwenkun.demo.library2.R.string.hello_world}")

        withStyledAttributes(
            android.R.style.TextAppearance,
            intArrayOf(android.R.attr.textSize)
        ) {
            getValue(0, typedValue)
        }
    }
}