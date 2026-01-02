package me.liwenkun.demo.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.liwenkun.demo.R
import java.util.Collections

class DragAdapter : RecyclerView.Adapter<DragAdapter.ViewHolder>() {

    private val items = mutableListOf<ListItem>()

    // 删除监听器
    var onItemDeleteListener: ((ListItem) -> Unit)? = null

    // 点击监听器
    var onItemClickListener: ((ListItem) -> Unit)? = null

    fun setData(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getItems(): List<ListItem> = items.toList()

    fun removeItem(position: Int) {
        if (position in 0 until items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_draggable, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tv_title = itemView.findViewById<TextView>(R.id.tv_title)
        private val tv_content = itemView.findViewById<TextView>(R.id.tv_content)
        private val iv_delete = itemView.findViewById<ImageView>(R.id.iv_delete)
        private val iv_drag_handle = itemView.findViewById<ImageView>(R.id.iv_drag_handle)

        fun bind(item: ListItem) {
            with(itemView) {
                tv_title.text = item.title
                tv_content.text = item.content

                // 设置背景色
                background.setTint(if (item.isSelected) 0xFFE3F2FD.toInt() else 0xFFFFFFFF.toInt())

                // 点击事件
                setOnClickListener {
                    onItemClickListener?.invoke(item)
                }

                // 删除按钮点击
                iv_delete.setOnClickListener {
                    onItemDeleteListener?.invoke(item)
                }

                // 拖拽手柄 - 长按开始拖拽
                iv_drag_handle.setOnLongClickListener { view ->
                    onDragListener?.onStartDrag(this@ViewHolder)
                    true
                }
            }
        }
    }

    // ItemTouchHelper 回调接口
    interface OnDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    var onDragListener: OnDragListener? = null
}