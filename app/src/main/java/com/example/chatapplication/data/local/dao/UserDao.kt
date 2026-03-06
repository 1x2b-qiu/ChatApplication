package com.example.chatapplication.data.local.dao // 数据访问对象包名

import androidx.room.Dao // Room DAO 注解
import androidx.room.Insert // Room 插入操作注解
import androidx.room.OnConflictStrategy // Room 冲突策略枚举
import androidx.room.Query // Room 查询操作注解
import androidx.room.Upsert
import com.example.chatapplication.data.local.entity.UserEntity // 用户数据库实体类
import kotlinx.coroutines.flow.Flow // 异步数据流

@Dao
interface UserDao {
    //插入用户
    @Upsert
    suspend fun insertUser(user: UserEntity)

    //根据用户id获取用户的安全信息
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

}
