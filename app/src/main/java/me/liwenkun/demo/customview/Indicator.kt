package me.liwenkun.demo.customview

import android.R.attr.radius
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import me.liwenkun.demo.R
import kotlin.math.abs
import androidx.core.content.withStyledAttributes

class Indicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var index = 0
    private var offset = 0f
    private var requiredWidth = 0
    private var radius: Int = 0
    private var margin: Int = 0
    private var focusLength: Int = 0
    private val focusedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val unFocusedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var num = 0

    init {
        context.withStyledAttributes(attrs, R.styleable.Indicator, defStyleAttr, 0) {
            radius = getDimensionPixelSize(R.styleable.Indicator_dotRadius, DEF_RADIUS)
            margin = getDimensionPixelSize(R.styleable.Indicator_dotMargin, DEF_MARGIN)
            focusLength = getDimensionPixelSize(
                R.styleable.Indicator_focusedDotLength,
                DEF_FOCUS_LENGTH
            )
            focusLength = (radius * 2).coerceAtLeast(focusLength)
            val focusedDotColor =
                getColor(R.styleable.Indicator_focusedDotColor, DEF_FOCUSED_DOT_COLOR)
            val unfocusedDotColor =
                getColor(R.styleable.Indicator_unfocusedDotColor, DEF_UNFOCUSED_DOT_COLOR)
            focusedPaint.color = focusedDotColor
            focusedPaint.style = Paint.Style.FILL_AND_STROKE
            unFocusedPaint.color = unfocusedDotColor
            unFocusedPaint.style = Paint.Style.FILL_AND_STROKE
        }
    }

    override fun onDraw(canvas: Canvas) {

        // 焦点指示器超出的长度
        val deltaLength = focusLength - radius * 2
        var startX = (width - requiredWidth) / 2
        for (i in 0 until num) {
            var focus = false
            var straightLength = 0
            if (i == index) {
                straightLength = (deltaLength * (1 - offset)).toInt()
                focus = abs(offset) < 0.5
            }
            if (i == index + 1) {
                straightLength = (deltaLength * offset).toInt()
                focus = abs(offset) >= 0.5
            }
            val l: Int = startX
            val t = 0
            val r = l + radius * 2 + straightLength
            val b: Int = radius * 2
            canvas.drawRoundRect(
                l.toFloat(),
                t.toFloat(),
                r.toFloat(),
                b.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                if (focus) focusedPaint else unFocusedPaint
            )
            startX = (r + margin)
        }
    }

    private fun setCurrent(index: Int, offset: Float) {
        this.index = index
        this.offset = offset
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        @SuppressLint("SwitchIntDef") val finalW = when (widthMode) {
            MeasureSpec.AT_MOST -> width.coerceAtMost(requiredWidth)
            MeasureSpec.EXACTLY -> width
            else -> requiredWidth
        }
        @SuppressLint("SwitchIntDef") val finalH = when (heightMode) {
            MeasureSpec.AT_MOST -> height.coerceAtMost(radius * 2)
            MeasureSpec.EXACTLY -> height
            else -> radius * 2
        }
        setMeasuredDimension(finalW, finalH)
    }

    fun setUpWithPager(viewPager: ViewPager) {
        viewPager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                setCurrent(position, positionOffset)
            }
        })
        if (viewPager.adapter != null) {
            update(viewPager.adapter)
        }
        viewPager.addOnAdapterChangeListener { _: ViewPager?, _: PagerAdapter?, newAdapter: PagerAdapter? ->
            update(
                newAdapter
            )
        }
    }

    private fun update(pagerAdapter: PagerAdapter?) {
        num = pagerAdapter?.count ?: 0
        requiredWidth =
            if (num == 0) 0 else (num - 1) * radius * 2 + focusLength + (num - 1) * margin
    }

    companion object {
        private const val DEF_RADIUS = 5
        private const val DEF_MARGIN = 15
        private const val DEF_FOCUS_LENGTH = 70
        private const val DEF_FOCUSED_DOT_COLOR = Color.GRAY
        private const val DEF_UNFOCUSED_DOT_COLOR = Color.LTGRAY
    }
}