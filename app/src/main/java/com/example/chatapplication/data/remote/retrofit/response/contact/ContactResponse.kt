package com.example.chatapplication.data.remote.retrofit.response.contact

data class ContactResponse (
    val id: String,
    val name: String,
    val avatar: String?,
    val phone: String,
    val gender: String
)