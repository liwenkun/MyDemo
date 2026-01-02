package me.liwenkun.demo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.liwenkun.demo.R
import me.liwenkun.demo.demoframework.DemoBaseFragment
import me.liwenkun.demo.libannotation.Demo
import java.util.Stack

@Demo(title = "拖拽排序")
class DragFragment : DemoBaseFragment() {

    // 在 MainActivity 中添加
    private val deletedItems = Stack<Pair<Int, ListItem>>()

// 在 initView 中添加按钮点击事件


    private fun undoDelete() {
        if (deletedItems.isNotEmpty()) {
            val (position, item) = deletedItems.pop()
            val currentItems = adapter.getItems().toMutableList()
            currentItems.add(position, item)
            adapter.setData(currentItems)
            Toast.makeText(requireContext(), "已恢复: ${item.title}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "没有可恢复的项目", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetList() {
        initData()
        deletedItems.clear()
        Toast.makeText(requireContext(), "列表已重置", Toast.LENGTH_SHORT).show()
    }

// 修改适配器的删除回调


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DragAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_drag, container, false).also {
            initView(it)
            initData()
        }
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)

        // 设置布局管理器
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 初始化适配器
        adapter = DragAdapter()
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.btn_undo).setOnClickListener {
            undoDelete()
        }

        view.findViewById<Button>(R.id.btn_reset).setOnClickListener {
            resetList()
        }

        // 初始化 ItemTouchHelper
        val callback = DragItemTouchHelper(adapter)

        // 设置回调
        callback.onDragCompleted = { from, to ->
            Toast.makeText(requireContext(), "从位置 $from 移动到位置 $to", Toast.LENGTH_SHORT).show()
        }

        callback.onSwipeCompleted = { position ->
            val item = adapter.getItems()[position]
            adapter.removeItem(position)
            Toast.makeText(requireContext(), "已删除: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // 设置拖拽监听器
        adapter.onDragListener = object : DragAdapter.OnDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(viewHolder)
            }
        }

        adapter.onItemDeleteListener = { item ->
            val position = adapter.getItems().indexOf(item)
            deletedItems.push(Pair(position, item))
            adapter.removeItem(position)
            Toast.makeText(requireContext(), "已删除: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        // 设置点击监听
        adapter.onItemClickListener = { item ->
            Toast.makeText(requireContext(), "点击: ${item.title}", Toast.LENGTH_SHORT).show()
        }

        // 设置删除监听
        adapter.onItemDeleteListener = { item ->
            val position = adapter.getItems().indexOf(item)
            adapter.removeItem(position)
            Toast.makeText(requireContext(), "按钮删除: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initData() {
        val items = mutableListOf<ListItem>()
        for (i in 1..20) {
            items.add(
                ListItem(
                    id = i,
                    title = "项目 $i",
                    content = "这是第 $i 个项目的描述内容",
                    isSelected = i % 3 == 0
                )
            )
        }
        adapter.setData(items)
    }
}