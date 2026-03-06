package com.example.chatapplication.data.remote.retrofit.response.auth

import com.example.chatapplication.data.remote.retrofit.response.contact.ContactResponse

data class LoginResponse(
    val code: Int,
    val message: String?,
    val userId: String?,
    val name: String?,
    val avatar: String?,
    val gender: String?,
    val lastLoginTime: Long?,
    val contacts: List<ContactResponse>?
)