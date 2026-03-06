package com.example.chatapplication.data.model


data class ContactApply (
    val contact: Contact,
    val applyId: Int,
    val status: String,
    val message: String,
    val createdAt: Long,
    val handledAt: Long?,
    val isMySent: Boolean
)