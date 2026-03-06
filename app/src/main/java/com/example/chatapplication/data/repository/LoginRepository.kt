package com.example.chatapplication.data.repository

interface LoginRepository {

    //通过手机号和密码登录
    suspend fun login(phone: String, passwordHash: String): Boolean
}

