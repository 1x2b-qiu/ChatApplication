package com.example.chatapplication.data.repository.impl

import com.example.chatapplication.data.local.dao.ConversationDao
import com.example.chatapplication.data.local.entity.ConversationEntity
import com.example.chatapplication.data.mapper.toDomain
import com.example.chatapplication.data.repository.ConversationRepository
import com.example.chatapplication.data.repository.SessionRepository
import com.example.chatapplication.data.model.Conversation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao, private val sessionRepository: SessionRepository
) : ConversationRepository {

    private var userId: String = ""
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun start() {
        scope.launch {
            userId = sessionRepository.getUserId()!!
        }
    }

    //读取所有会话
    override fun observeConversations(): Flow<List<Conversation>> {
        return conversationDao.observeConversations(userId).map { conversations ->
            conversations.map { it.toDomain() }
        }
    }

    //更新会话置顶状态
    override suspend fun updateConversationIsPinned(id: String, isPinned: Boolean) {
        conversationDao.updateIsPinned(id, isPinned, userId)
    }

    //清空未读数
    override suspend fun resetConversationUnreadCount(id: String) {
        //用户读取后清空未读数
        conversationDao.resetUnreadCount(id, userId)
    }

    //创建一个新的会话
    override suspend fun createConversation(contactId: String) {
        //先查看是否有这个会话
        val conversation = conversationDao.getConversationById(contactId, userId)
        if (conversation == null) {
            val newConversation = ConversationEntity(
                id = contactId,
                ownerId = userId,
                contactId = contactId,
                lastMessage = null,
                lastMessageTime = null,
                unreadCount = 0
            )
            conversationDao.insertConversation(newConversation)
        }
    }

    //删除会话
    override suspend fun deleteConversation(conversationId: String) {
        conversationDao.deleteConversation(conversationId, userId)
    }

}