package com.example.chatapplication.data.remote.retrofit.response.contactApply

data class GetMySentContactResponse(
    val code: Int,
    val message: String,
    val list: List<ContactApplyResponse>?
)
