package com.example.chatapplication.data.repository

import com.example.chatapplication.data.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun start()

    //读取所有会话
    fun observeConversations(): Flow<List<Conversation>>

    //更新会话置顶状态
    suspend fun updateConversationIsPinned(id: String, isPinned: Boolean)

    //清空未读数
    suspend fun resetConversationUnreadCount(id: String)

    //创建一个新的会话
    suspend fun createConversation(contactId: String)

    //删除一个会话
    suspend fun deleteConversation(conversationId: String)
}

