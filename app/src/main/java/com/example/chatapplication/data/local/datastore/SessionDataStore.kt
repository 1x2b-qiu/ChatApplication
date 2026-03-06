package com.example.chatapplication.data.local.datastore // 本地会话数据存储包名

import androidx.datastore.core.DataStore // DataStore 核心接口
import androidx.datastore.preferences.core.Preferences // Preferences 类型定义
import androidx.datastore.preferences.core.edit // 数据修改扩展函数
import androidx.datastore.preferences.core.stringPreferencesKey // 字符串键值对定义
import kotlinx.coroutines.flow.Flow // 异步流
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map // 流转换操作符
import javax.inject.Inject // Hilt 注入注解

class SessionDataStore @Inject constructor( // 支持注入的会话数据存储类
    private val dataStore: DataStore<Preferences> // 注入 DataStore 实例
) {
    companion object { // 静态常量定义
        private val USER_ID_KEY = stringPreferencesKey("user_id") // 定义用户 ID 的 Key
    }

    suspend fun saveUserId(userId: String) { // 异步保存用户 ID
        dataStore.edit { preferences -> // 开启编辑事务
            preferences[USER_ID_KEY] = userId // 写入用户 ID
        }
    }

    suspend fun clearUserId() { // 异步清除用户 ID
        dataStore.edit { preferences -> // 开启编辑事务
            preferences.remove(USER_ID_KEY) // 移除用户 ID
        }
    }

    suspend fun getUserId(): String? =
        dataStore.data.first()[USER_ID_KEY]

    fun observeUserId(): Flow<String?> = // 监听用户 ID 变化
        dataStore.data.map { preferences -> // 转换原始数据流
            preferences[USER_ID_KEY] // 返回用户 ID 值
        }

}
