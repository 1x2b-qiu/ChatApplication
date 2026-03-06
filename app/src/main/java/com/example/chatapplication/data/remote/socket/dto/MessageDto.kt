package com.example.chatapplication.data.remote.socket.dto

data class MessageDto(
    val id: String,
    val senderId: String,
    val targetId: String,
    val timestamp: Long,
    val content: String
)