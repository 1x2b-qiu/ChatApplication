package com.example.chatapplication.data.remote.retrofit.response.chat

data class UploadImageResponse(
    val code: Int,
    val message: String? = null,
    val url: String? = null
)