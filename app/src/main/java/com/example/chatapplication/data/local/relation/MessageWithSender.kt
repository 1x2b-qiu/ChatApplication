package com.example.chatapplication.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.chatapplication.data.local.entity.ContactEntity
import com.example.chatapplication.data.local.entity.MessageEntity

data class MessageWithSender(
    //内嵌
    @Embedded val message: MessageEntity,
    @Relation(
        parentColumn = "senderId", // 谁发的这条消息
        entityColumn = "id"        // 对应联系人表的 ID
    )
    val sender: ContactEntity?     // 这样你就直接拿到了发送者的头像和昵称
)