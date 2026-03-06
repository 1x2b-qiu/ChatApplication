package com.example.chatapplication.ui.screen.main

sealed class BottomNavItem(
    val route: String,
    val label: String
) {
    object Conversations : BottomNavItem("conversations", "消息")
    object Contacts : BottomNavItem("contacts", "好友")
    object Mine : BottomNavItem("mine", "我的")

    // 方便在 UI 中遍历
    companion object {
        val items = listOf(Conversations, Contacts, Mine)
    }

}