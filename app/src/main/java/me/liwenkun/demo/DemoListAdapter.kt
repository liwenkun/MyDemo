package me.liwenkun.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.liwenkun.demo.demoframework.DemoBook
import me.liwenkun.demo.demoframework.DemoBook.DemoItem

class DemoListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface Callback : DemoItemViewHolder.Callback, CategoryViewHolder.Callback

    private var callback: Callback? = null
    private val subCategories: MutableList<DemoBook.Category> = ArrayList()
    private val demoItems: MutableList<DemoItem> = ArrayList()
    private var host: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_CATEGORY) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.demo_category, parent, false)
            CategoryViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.demo_item, parent, false)
            DemoItemViewHolder(itemView)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        host = recyclerView
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CategoryViewHolder) {
            holder.bindData(getItem(position) as DemoBook.Category)
            holder.setCallback(callback)
        } else if (holder is DemoItemViewHolder) {
            holder.bindData(getItem(position) as DemoItem)
            holder.setCallback(callback)
        }
    }

    fun update(currentCategory: DemoBook.Category) {
        val oldCount = itemCount
        subCategories.clear()
        demoItems.clear()
        notifyItemRangeRemoved(0, oldCount)
        host?.also {
            subCategories.addAll(currentCategory.categories)
            demoItems.addAll(currentCategory.demoItems)
            val newCount = itemCount
            notifyItemRangeInserted(0, newCount)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        host = null
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is DemoBook.Category) TYPE_CATEGORY else TYPE_DEMO_ITEM
    }

    private fun getItem(position: Int): DemoBook.Item {
        return if (position < subCategories.size) {
            subCategories[position]
        } else {
            demoItems[position - subCategories.size]
        }
    }

    override fun getItemCount(): Int {
        return subCategories.size + demoItems.size
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        interface Callback {
            fun onCategoryClick(view: View, category: DemoBook.Category, position: Int)
        }

        private var callback: Callback? = null
        private var category: DemoBook.Category? = null
        private val name: TextView

        init {
            name = itemView.findViewById(R.id.name)
            itemView.setOnClickListener {
                callback?.onCategoryClick(itemView, category!!, adapterPosition)
            }
        }

        fun bindData(category: DemoBook.Category) {
            this.category = category
            name.text = category.name
        }

        fun setCallback(callback: Callback?) {
            this.callback = callback
        }
    }

    class DemoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        interface Callback {
            fun onDemoItemClick(view: View, demoItem: DemoItem, position: Int)
        }

        private var callback: Callback? = null
        private var demoItem: DemoItem? = null
        private val name: TextView

        init {
            name = itemView.findViewById(R.id.name)
            itemView.setOnClickListener {
                callback?.onDemoItemClick(itemView, demoItem!!, adapterPosition)
            }
        }

        fun bindData(demoItem: DemoItem) {
            this.demoItem = demoItem
            name.text = demoItem.name
        }

        fun setCallback(callback: Callback?) {
            this.callback = callback
        }
    }

    companion object {
        private const val TYPE_CATEGORY = 0
        private const val TYPE_DEMO_ITEM = 1
    }
}