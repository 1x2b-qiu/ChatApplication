package com.example.chatapplication.data.remote.retrofit.response.contactApply

data class ApplyContactResponse(
    val code: Int,
    val message: String,
    val contactApplyResponse: ContactApplyResponse?
)