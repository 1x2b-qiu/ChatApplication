package com.example.chatapplication.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "conversations",
    primaryKeys = ["id", "ownerId"],
    foreignKeys = [
        ForeignKey(
            entity = ContactEntity::class,
            parentColumns = ["id", "ownerId"],
            childColumns = ["contactId", "ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["contactId", "ownerId"])]
)
data class ConversationEntity(
    val id: String,          // 会话唯一ID（重要）
    val ownerId: String,
    val contactId: String,                 // 对方用户ID / 群ID
    val lastMessage: String?,             // 最后一条消息内容（摘要）
    val lastMessageTime: Long?,             // 最后一条消息时间戳
    val isPinned: Boolean = false,         // 是否置顶
    val unreadCount: Int = 0              // 未读消息数
)