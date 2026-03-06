package com.example.chatapplication.data.repository // 仓库层包名

import com.example.chatapplication.data.model.Gender

interface RegisterRepository { // 注册业务仓库接口
    //注册验证
    suspend fun register(phone: String,passwordHash: String) : Boolean

    //提交个人资料
    suspend fun completeProfile(
        name: String,
        gender: Gender,
        avatar: String
    ):Boolean

}
