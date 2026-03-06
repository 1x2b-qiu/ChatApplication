package com.example.chatapplication.data.remote.retrofit.api

import com.example.chatapplication.data.remote.retrofit.request.contactApply.ApplyContactRequest
import com.example.chatapplication.data.remote.retrofit.request.contactApply.HandleContactRequest
import com.example.chatapplication.data.remote.retrofit.response.BaseResponse
import com.example.chatapplication.data.remote.retrofit.response.contactApply.ApplyContactResponse
import com.example.chatapplication.data.remote.retrofit.response.contactApply.GetMyReceivedContactResponse
import com.example.chatapplication.data.remote.retrofit.response.contactApply.GetMySentContactResponse
import com.example.chatapplication.data.remote.retrofit.response.contactApply.SearchContactResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ContactApplyApi {

    // 搜索好友
    @GET("contact/search")
    suspend fun searchContact(
        @Query("phone") phone: String,
        @Query("userId") userId: String
    ): SearchContactResponse

    // 申请添加好友
    @POST("contact/apply")
    suspend fun applyContact(@Body request: ApplyContactRequest): ApplyContactResponse

    // 处理好友申请
    @POST("contact/handle")
    suspend fun handleContact(@Body request: HandleContactRequest): BaseResponse

    // 获取我的申请记录
    @GET("contact/apply/sent")
    suspend fun getMySentContact(@Query("userId") userId: String): GetMySentContactResponse

    //获取我的接收记录
    @GET("contact/apply/received")
    suspend fun getMyReceivedContact(@Query("userId") userId: String): GetMyReceivedContactResponse
}