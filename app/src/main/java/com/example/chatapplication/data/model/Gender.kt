package com.example.chatapplication.data.model

enum class Gender { //性别枚举
    MALE,
    FEMALE,
    UNKNOWN;

    companion object{
        fun fromString(value: String): Gender{
            return when (value?.uppercase()) { // 使用 uppercase() 忽略大小写
                "MALE" -> MALE
                "FEMALE" -> FEMALE
                else -> UNKNOWN
            }
        }
    }
}