package com.example.chatapplication.data.remote.retrofit.request.contactApply

data class ApplyContactRequest (
    val userId: String,
    val contactId: String,
    val message: String
)