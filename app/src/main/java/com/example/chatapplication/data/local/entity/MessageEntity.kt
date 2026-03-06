package com.example.chatapplication.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "messages",
    primaryKeys = ["id", "ownerId"],
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id", "ownerId"],
            childColumns = ["conversationId", "ownerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id", "ownerId"],
            childColumns = ["senderId", "ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [ //联合索引
        Index("conversationId", "ownerId"),
        Index("senderId", "ownerId")
    ]
)
data class MessageEntity(
    val id: String,
    val ownerId: String,    //所属人
    val conversationId: String,
    val senderId: String, // 谁发的
    val targetId: String, // 发给谁
    val isMe: Boolean,  //是我发送的吗
    val timestamp: Long,    //发送时间
    val content: String
)
