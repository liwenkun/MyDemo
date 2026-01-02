package me.liwenkun.demo.customview

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.content.withStyledAttributes
import androidx.core.view.forEach
import me.liwenkun.demo.R
import kotlin.math.max

class FlowLayout: ViewGroup {

    private var View.location : IntArray?
        get() = this.getTag(R.id.tag_location) as? IntArray
        set(value) {
            this.setTag(R.id.tag_location, value)
        }

    var itemSpacing: Int = 0
    var lineSpacing: Int = 0
    var overflowBehavior: Int = OVERFLOW_KEEP
    var constrainChildHeight: Boolean = false
    val line = mutableListOf<View>()

    companion object {
        const val OVERFLOW_KEEP = 0
        const val OVERFLOW_ELLIPSIZE = 1
        const val OVERFLOW_SKIP = 2
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.withStyledAttributes(attrs, R.styleable.MyFlowLayout) {
            overflowBehavior = getInt(R.styleable.MyFlowLayout_overflowBehavior, OVERFLOW_KEEP)
            itemSpacing = getDimensionPixelSize(R.styleable.MyFlowLayout_itemSpacing, 0)
            constrainChildHeight = getBoolean(R.styleable.MyFlowLayout_constrainChildHeight, true)
            lineSpacing = getDimensionPixelSize(R.styleable.MyFlowLayout_lineSpacing, 0)
        }
    }

    override fun measureChildWithMargins(
        child: View?,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ) {
        if (constrainChildHeight) {
            super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
        } else {
            val lp = child!!.layoutParams as MarginLayoutParams

            val childWidthMeasureSpec = getChildMeasureSpec(
                parentWidthMeasureSpec,
                paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width
            )
            // 不给子视图高度做限制，想多大就多大，我们后面根据 overflowBehavior 再做决策
            val childHeightMeasureSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 1,
                MeasureSpec.AT_MOST), 0, lp.height)

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED)
            Int.MAX_VALUE else MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        // layoutLeft 为下一个子视图的用于布局的 left 位置，不考虑其 marginStart 占据的空间
        var layoutLeft = paddingLeft
        // layoutLeft 为下一个子视图的用于布局的 top 位置，不考虑其 marginTop 占据的空间
        var layoutTop = paddingTop
        // 当前行的最大高度
        var lineHeight = 0
        // 子视图 right 的理论最大值
        val maxRight = width - paddingRight
        // 子视图 bottom 的理论最大值
        val maxBottom = height - paddingBottom
        // 所有子视图 right 的实际最大值
        var maxChildRight = paddingRight

        line.clear()

        for (i in 0 until childCount) {

            val originalLayoutLeft = layoutLeft
            val originalLayoutTop = layoutTop
            val originLineHeight = lineHeight

            val child = getChildAt(i)

            val marginStart = (child.layoutParams as MarginLayoutParams).marginStart
            val marginEnd = (child.layoutParams as MarginLayoutParams).marginEnd

            // 先用本行的剩余空间测量子视图
            measureChildWithMargins(child, widthMeasureSpec,
                layoutLeft - paddingLeft - itemSpacing, heightMeasureSpec, layoutTop - paddingTop)

            var childWidth = child.measuredWidth
            var childHeight = child.measuredHeight

            var layoutInNewLine = false

            if (layoutLeft + childWidth + marginStart + marginEnd > maxRight) {
                // 子视图 right 超出理论最大值，尝试换行测量
                layoutLeft = paddingLeft
                layoutTop += lineHeight + lineSpacing

                measureChildWithMargins(child, widthMeasureSpec,
                    0, heightMeasureSpec, layoutTop - paddingTop)

                childWidth = child.measuredWidth
                childHeight = child.measuredHeight

                // 换行后子视图 right 依然超出理论最大值
                if (layoutLeft + childWidth + marginStart + marginEnd > maxRight) {
                    when (overflowBehavior) {
                        OVERFLOW_ELLIPSIZE -> {
                            layoutTop = originalLayoutTop
                            lineHeight = originLineHeight
                            break
                        }
                        OVERFLOW_SKIP -> {
                            layoutLeft = originalLayoutLeft
                            layoutTop = originalLayoutTop
                            lineHeight = originLineHeight
                            continue
                        }
                    }
                }

                layoutInNewLine = true
            }

            // 子视图 bottom 超出理论最大值
            if (layoutTop + childHeight > maxBottom) {
                when (overflowBehavior) {
                    OVERFLOW_ELLIPSIZE -> {
                        layoutTop = originalLayoutTop
                        lineHeight = originLineHeight
                        break
                    }
                    OVERFLOW_SKIP -> {
                        layoutLeft = originalLayoutLeft
                        layoutTop = originalLayoutTop
                        lineHeight = originLineHeight
                        continue
                    }
                }
            }

            if (layoutInNewLine) {
                line.forEach { view ->
                    view.location?.let { location ->
                        when ((view.layoutParams as LayoutParams).gravity) {
                            Gravity.TOP -> {
                                // do nothing
                            }
                            Gravity.CENTER_VERTICAL -> {
                                location[1] = location[1] + (lineHeight - view.measuredHeight) / 2
                            }
                            Gravity.BOTTOM -> {
                                location[1] = location[1] + lineHeight - view.measuredHeight
                            }
                        }
                    }
                }
                lineHeight = 0
                line.clear()
            } else {
                line.add(child)
            }


            // 子视图 left 位置
            val childLeft = layoutLeft + marginStart
            // 子视图 top 位置
            val childTop = layoutTop

            // 将子视图位置保存到 tag 中，供 onLayout() 使用
            child.location = intArrayOf(childLeft, childTop)

            // 更新 maxChildRight 和 layoutLeft
            maxChildRight = max(maxChildRight, layoutLeft + child.measuredWidth)
            layoutLeft += marginStart + childWidth + marginEnd + itemSpacing


            lineHeight = max(lineHeight, childHeight)
        }

        // 根据内容宽高和自身宽高计算出最终的宽高
        val finalHeight = resolveSize(layoutTop + lineHeight + paddingBottom, heightMeasureSpec)
        val finalWidth = resolveSize(maxChildRight + paddingRight, widthMeasureSpec)

        setMeasuredDimension(finalWidth, finalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        forEach {
            it.location?.apply {
                it.layout(
                    get(0), get(1),
                    get(0)+ it.measuredWidth,
                    get(1) + it.measuredHeight
                )
            }
        }
    }

    class LayoutParams : MarginLayoutParams {

        var gravity = Gravity.TOP

        constructor(width: Int, height: Int) : super(width, height)

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            context.withStyledAttributes(attrs = R.styleable.MyFlowLayout_LayoutParams, set = attrs) {
                gravity = getInt(R.styleable.MyFlowLayout_LayoutParams_android_layout_gravity, Gravity.TOP)
            }
        }

        constructor(p: ViewGroup.LayoutParams) : super(p)

        constructor(p: MarginLayoutParams) : super(p) {
            if (p is LayoutParams) {
                gravity = p.gravity
            }
        }

    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams
    = if (p is MarginLayoutParams) LayoutParams(p) else LayoutParams(p)

    override fun checkLayoutParams(p: ViewGroup.LayoutParams) = p is LayoutParams

}