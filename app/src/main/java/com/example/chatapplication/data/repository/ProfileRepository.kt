package com.example.chatapplication.data.repository // 仓库接口包名

import com.example.chatapplication.data.model.Contact
import com.example.chatapplication.data.model.Gender
import kotlinx.coroutines.flow.Flow // 异步流

interface ProfileRepository { // 个人资料仓库接口

    fun start()

    //获取我的资料
    fun observeProfile(userId: String): Flow<Contact>

    //更新我的资料
    suspend fun updateProfile(
        id: String,
        name: String? = null,
        gender: Gender? = null,
        avatar: String? = null,
    ): Boolean
}
