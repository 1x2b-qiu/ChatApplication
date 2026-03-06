package com.example.chatapplication.data.remote.retrofit.response.contactApply

data class GetMyReceivedContactResponse(
    val code: Int,
    val message: String,
    val list: List<ContactApplyResponse>?
)
