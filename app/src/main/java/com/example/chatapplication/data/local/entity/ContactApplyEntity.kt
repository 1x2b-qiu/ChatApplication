package com.example.chatapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.chatapplication.data.local.converter.GenderConverter
import com.example.chatapplication.data.model.Gender

@Entity(tableName = "contact_apply")
@TypeConverters(GenderConverter::class) // 指定该类使用的类型转换器
data class ContactApplyEntity(
    @PrimaryKey
    val applyId: Int,   //申请ID
    val contactId: String,  //对方ID
    val ownerId: String,    //所属人ID
    val name: String,
    val avatar: String,
    val phone: String,
    val gender: Gender,
    val status: String,     //pending,accepted,rejected
    val message: String,    //申请信息
    val createdAt: Long,    //申请时间
    val handledAt: Long?,   //处理时间
    val isMySent: Boolean   //是我发送的申请吗
)