package com.example.chatapplication.data.remote.retrofit.request.contactApply

data class HandleContactRequest(
    val applyId: Int,
    val action: String,
    val handleAt: Long
)
