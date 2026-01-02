package me.liwenkun.demo.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.withClip

class ClipLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var clip = 0f
    private val path: Path = Path()

    fun setClip(clip: Float) {
        this.clip = clip
        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        path.reset()
        val x = clip * width
        path.moveTo(0f, 0f)
        path.lineTo(x, 0f)
        path.quadTo(x + height, height / 2f, x, height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.lineTo(0f, 0f)
        canvas.withClip(path) {
            super.dispatchDraw(canvas)
        }
    }
}