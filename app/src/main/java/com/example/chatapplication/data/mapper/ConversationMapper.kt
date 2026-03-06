package com.example.chatapplication.data.mapper

import com.example.chatapplication.data.local.entity.ConversationEntity
import com.example.chatapplication.data.local.relation.ConversationWithContact
import com.example.chatapplication.data.model.Conversation

fun ConversationWithContact.toDomain(): Conversation = Conversation(
    id = this.conversation.id,
    contact = this.contact.toDomain(),
    lastMessage = this.conversation.lastMessage,
    lastMessageTime = this.conversation.lastMessageTime,
    unreadCount = this.conversation.unreadCount,
    isPinned = this.conversation.isPinned
)


fun Conversation.toEntity(ownerId: String): ConversationEntity = ConversationEntity(
    id = this.id,
    ownerId = ownerId,
    contactId = this.contact.id,
    lastMessage = this.lastMessage,
    lastMessageTime = this.lastMessageTime,
    unreadCount = this.unreadCount,
    isPinned = this.isPinned
)
