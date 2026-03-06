package com.example.chatapplication.data.remote.retrofit.response.contact

data class GetContactsResponse (
    val code: Int,
    val message: String,
    val contacts: List<ContactResponse>?
)

