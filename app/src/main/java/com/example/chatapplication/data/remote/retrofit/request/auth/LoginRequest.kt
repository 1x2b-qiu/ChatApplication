package com.example.chatapplication.data.remote.retrofit.request.auth

data class LoginRequest (
    val phone: String,
    val passwordHash: String
)