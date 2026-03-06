package com.example.chatapplication.data.repository.impl // 仓库实现层包名

import com.example.chatapplication.data.local.datastore.SessionDataStore // 会话数据存储
import com.example.chatapplication.data.repository.SessionRepository // 会话仓库接口
import kotlinx.coroutines.flow.Flow // 异步流
import javax.inject.Inject // Hilt 注入注解

class SessionRepositoryImpl @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : SessionRepository {

    //登录
    override suspend fun login(userId: String) {
        sessionDataStore.saveUserId(userId = userId)
    }

    //退出登录
    override suspend fun logout() {
        sessionDataStore.clearUserId()
    }

    //一次性获取用户Id
    override suspend fun getUserId(): String? = sessionDataStore.getUserId()

    //持续获取用户Id
    override fun observeUserId(): Flow<String?> =
        sessionDataStore.observeUserId()

}
