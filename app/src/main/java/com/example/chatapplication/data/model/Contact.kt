package com.example.chatapplication.data.model


data class Contact (
    val id: String,
    val name: String,
    val avatar: String?,
    val phone: String,
    val gender: Gender,
    val remarks: String? = null
)