package com.example.chatapplication.data.repository

import android.net.Uri
import com.example.chatapplication.data.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun start()

    //获取所有消息
    fun observeMessages(conversationId: String): Flow<List<Message>>

    //发送消息
    suspend fun sendMessage(targetId: String, content: String)

    //设置当前活跃conversationId
    fun setActiveConversation(conversationId: String?)


    suspend fun uploadImage(uri: Uri): Result<String>

}
