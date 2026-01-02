package me.liwenkun.demo.view

data class ListItem(
    val id: Int,
    val title: String,
    val content: String,
    var isSelected: Boolean = false
)