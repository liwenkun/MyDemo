package me.liwenkun.demo.view

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragItemTouchHelper(
    private val adapter: DragAdapter,
    private val dragDirections: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    private val swipeDirections: Int = ItemTouchHelper.LEFT
) : ItemTouchHelper.Callback() {

    private var fromPosition: Int = -1
    private var toPosition: Int = -1

    // 拖拽完成回调
    var onDragCompleted: ((from: Int, to: Int) -> Unit)? = null

    // 删除完成回调
    var onSwipeCompleted: ((position: Int) -> Unit)? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(dragDirections, swipeDirections)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition

        if (fromPosition == -1) {
            fromPosition = from
        }
        toPosition = to

        // 移动数据
        adapter.moveItem(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        onSwipeCompleted?.invoke(position)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                // 拖拽开始时改变背景色
                viewHolder?.itemView?.apply {
                    background.setTint(0xFFE8F5E8.toInt())
                }
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        // 恢复背景色
        viewHolder.itemView.background.setTint(0xFFFFFFFF.toInt())

        // 拖拽完成
        if (fromPosition != -1 && toPosition != -1 && fromPosition != toPosition) {
            onDragCompleted?.invoke(fromPosition, toPosition)
        }

        fromPosition = -1
        toPosition = -1
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_SWIPE -> {
                // 侧滑时的透明度变化
                val alpha = 1f - Math.abs(dX) / viewHolder.itemView.width
                viewHolder.itemView.alpha = alpha
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            else -> super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun isLongPressDragEnabled(): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = true
}