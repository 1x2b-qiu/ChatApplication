package com.example.chatapplication.data.remote.retrofit.response.auth

data class RegisterResponse(
    val code: Int,
    val message: String?,
    val userId: String?,
    val lastLoginTime: Long?
)