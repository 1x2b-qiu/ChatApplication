package com.example.chatapplication.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.chatapplication.data.local.entity.ContactEntity
import com.example.chatapplication.data.local.entity.ConversationEntity

data class ConversationWithContact (
    @Embedded val conversation: ConversationEntity,
    @Relation(
        parentColumn = "contactId",
        entityColumn = "id"
    )
    val contact: ContactEntity
)