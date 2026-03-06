package com.example.chatapplication.notification.model

data class NotificationMessage (
    val messageId: String,
    val conversationId: String,
    val targetId: String,
    val targetName: String,
    val content: String,
    val timestamp: Long
)