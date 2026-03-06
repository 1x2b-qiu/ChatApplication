package com.example.chatapplication.data.remote.retrofit.response.contactApply

import com.google.gson.annotations.SerializedName

data class ContactApplyResponse(
    val applyId: Int,
    val contactId: String,
    val name: String,
    val phone: String,
    val avatar: String,
    val gender: String,
    val status: String,
    val message: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("handled_at")
    val handledAt: Long?
)
