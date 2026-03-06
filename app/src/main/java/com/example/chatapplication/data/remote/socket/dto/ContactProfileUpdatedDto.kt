package com.example.chatapplication.data.remote.socket.dto

data class ContactProfileUpdatedDto (
    val userId: String,
    val name: String?,
    val avatar: String?,
    val gender: String?
)
