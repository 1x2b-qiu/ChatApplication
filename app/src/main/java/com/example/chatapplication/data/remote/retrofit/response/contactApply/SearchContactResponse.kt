package com.example.chatapplication.data.remote.retrofit.response.contactApply

import com.example.chatapplication.data.remote.retrofit.response.contact.ContactResponse

data class SearchContactResponse (
    val code: Int,
    val message: String,
    val contact: ContactResponse?
)