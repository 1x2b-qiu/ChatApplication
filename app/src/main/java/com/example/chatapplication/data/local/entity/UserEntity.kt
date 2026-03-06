package com.example.chatapplication.data.local.entity // 数据库实体类包名

import androidx.room.Entity // Room 实体类注解
import androidx.room.PrimaryKey // Room 主键注解

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String, // 用户唯一 ID
    val passwordHash: String, // 加密后的登录密码
    val lastLoginTime: Long, // 最后一次登录时间
    val isActive: Boolean   //是否活跃
)
