package com.example.chatapplication.data.remote.retrofit.api

import com.example.chatapplication.data.remote.retrofit.response.contact.GetContactsResponse
import com.example.chatapplication.data.remote.retrofit.response.contactApply.SearchContactResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ContactApi {

    //拉取所有好友信息用于在线刷新
    @GET("contact/contacts")
    suspend fun getContacts(@Query("userId") userId: String): GetContactsResponse

}
