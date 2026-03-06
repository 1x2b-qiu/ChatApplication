package com.example.chatapplication.data.remote.retrofit.request.auth

data class RegisterRequest(
    val phone: String,
    val passwordHash: String
)