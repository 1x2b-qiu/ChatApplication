package com.example.chatapplication.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverters
import com.example.chatapplication.data.local.converter.GenderConverter
import com.example.chatapplication.data.model.Gender

@Entity(
    tableName = "contacts",
    primaryKeys = ["id", "ownerId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
@TypeConverters(GenderConverter::class) // 指定该类使用的类型转换器
data class ContactEntity(
    val id: String,
    val ownerId: String,    //所属人
    val name: String,
    val avatar: String?,
    val phone: String,
    val gender: Gender = Gender.UNKNOWN,
    val remarks: String? = null     //备注
)