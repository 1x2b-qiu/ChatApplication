package com.example.chatapplication.data.remote.retrofit.api

import com.example.chatapplication.data.remote.retrofit.response.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileApi {
    //更新个人资料
    @Multipart
    @POST("profile/updateProfile")
    suspend fun updateProfile(
        @Part("userId") userId: RequestBody,
        @Part("name") name: RequestBody? = null,
        @Part("gender") gender: RequestBody? = null,
        @Part avatar: MultipartBody.Part? = null
    ): BaseResponse
}