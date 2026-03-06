package com.example.chatapplication.data.mapper

import com.example.chatapplication.data.local.entity.MessageEntity
import com.example.chatapplication.data.local.relation.MessageWithSender

import com.example.chatapplication.data.model.Message


fun MessageWithSender.toDomain(): Message = Message(
    id = this.message.id,
    conversationId = this.message.conversationId,
    isMe = this.message.isMe,
    timestamp = this.message.timestamp,
    content = this.message.content,
    senderId = this.message.senderId,
    targetId = this.message.targetId,
    senderAvatar = this.sender?.avatar
)

fun Message.toEntity(ownerId: String): MessageEntity = MessageEntity(
    id = this.id,
    ownerId = ownerId,
    conversationId = this.conversationId,
    isMe = this.isMe,
    timestamp = this.timestamp,
    content = this.content,
    senderId = this.senderId,
    targetId = this.targetId
)
