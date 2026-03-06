package com.example.chatapplication.data.repository // 仓库接口包名

import kotlinx.coroutines.flow.Flow // 异步流

interface SessionRepository { // 会话仓库接口
    suspend fun login(userId: String) // 执行登录并保存用户 ID
    suspend fun logout() // 执行退出登录并清除数据
    suspend fun getUserId(): String?    //一次性获取用户ID
    fun observeUserId(): Flow<String?> // 监听用户 ID 变化流
}
