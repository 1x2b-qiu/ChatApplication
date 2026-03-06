package com.example.chatapplication.data.remote.retrofit.api

import com.example.chatapplication.data.remote.retrofit.request.auth.LoginRequest
import com.example.chatapplication.data.remote.retrofit.request.auth.RegisterRequest
import com.example.chatapplication.data.remote.retrofit.response.BaseResponse
import com.example.chatapplication.data.remote.retrofit.response.auth.LoginResponse
import com.example.chatapplication.data.remote.retrofit.response.auth.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApi {

    //登录接口
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    //注册接口
    @POST("auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): RegisterResponse

    //注册后提交资料
    @Multipart
    @POST("auth/completeProfile")
    suspend fun completeProfile(
        @Part("userId") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part avatar: MultipartBody.Part?
    ): BaseResponse

}