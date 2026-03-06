package com.example.chatapplication.data.model

data class Conversation(
    val id: String,          // 会话唯一ID（重要）
    val contact: Contact,
    val lastMessage: String?,               // 最后一条消息内容（摘要）
    val lastMessageTime: Long?,             // 最后一条消息时间戳
    val isPinned: Boolean = false,         // 是否置顶
    val unreadCount: Int = 0              // 未读消息数
)
