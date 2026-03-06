package com.example.chatapplication.data.remote.socket.dto

data class ContactAcceptedDto (
    val applyId: Int,
    val contactId: String,
    val name: String,
    val avatar: String,
    val phone: String,
    val gender: String,
    val status: String,
    val message: String,
    val createdAt: Long,
    val handledAt: Long,
    val isMySent: Boolean
)
