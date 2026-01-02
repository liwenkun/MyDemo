package me.liwenkun.demo.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import me.liwenkun.demo.App.Companion.get

fun px(dp: Int): Int {
    val density = get().resources.displayMetrics.density
    return (dp * density).toInt()
}

fun Context.post(block: () -> Unit) {
    get().post(block)
}

@ColorInt
fun Context.color(@ColorRes color: Int): Int =
    ResourcesCompat.getColor(resources, color, theme)
