package com.example.chatapplication.data.model

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String, // 新增
    val targetId: String, // 新增
    val senderAvatar: String?, // 新增
    val isMe: Boolean,
    val timestamp: Long,
    val content: String
)
