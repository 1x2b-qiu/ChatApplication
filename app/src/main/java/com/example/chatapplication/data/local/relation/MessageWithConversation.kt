package com.example.chatapplication.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.chatapplication.data.local.entity.ConversationEntity
import com.example.chatapplication.data.local.entity.MessageEntity

data class MessageWithConversation (
    @Embedded val message: MessageEntity,
    @Relation(
        parentColumn = "conversationId",
        entityColumn = "id"
    )
    val conversation: ConversationEntity

)