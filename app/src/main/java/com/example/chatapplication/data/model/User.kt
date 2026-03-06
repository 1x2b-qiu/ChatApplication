package com.example.chatapplication.data.model

data class User(
    val id: String, //用户ID
    val name: String,
    val avatar: String?,
    val phone: String,
    val gender: Gender = Gender.UNKNOWN
)