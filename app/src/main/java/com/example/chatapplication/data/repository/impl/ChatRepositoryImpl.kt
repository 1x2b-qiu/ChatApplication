package com.example.chatapplication.data.repository.impl

import android.content.Context
import android.net.Uri
import com.example.chatapplication.data.local.dao.ContactDao
import com.example.chatapplication.data.local.dao.ConversationDao
import com.example.chatapplication.data.local.dao.MessageDao
import com.example.chatapplication.data.local.entity.ContactEntity
import com.example.chatapplication.data.local.entity.ConversationEntity
import com.example.chatapplication.data.local.entity.MessageEntity
import com.example.chatapplication.data.mapper.toDomain
import com.example.chatapplication.data.remote.socket.SocketEvent
import com.example.chatapplication.data.remote.socket.SocketManager
import com.example.chatapplication.data.remote.socket.dto.MessageDto
import com.example.chatapplication.data.repository.ChatRepository
import com.example.chatapplication.data.repository.SessionRepository
import com.example.chatapplication.data.model.Gender
import com.example.chatapplication.data.model.Message
import com.example.chatapplication.data.remote.retrofit.api.ChatApi
import com.example.chatapplication.notification.NotificationDispatcher
import com.example.chatapplication.notification.model.NotificationMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.IllegalArgumentException
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val contactDao: ContactDao,
    private val sessionRepository: SessionRepository,
    private val conversationDao: ConversationDao,
    private val chatApi: ChatApi,
    private val socketManager: SocketManager,
    private val notificationDispatcher: NotificationDispatcher,
    @ApplicationContext private val context: Context
) : ChatRepository {

    //当前进入的conversation
    private var activeConversationId: String? = null

    private var userId: String = ""

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun start() {
        scope.launch {
            userId = sessionRepository.getUserId()!!
            observeSocketEvents()
        }
    }

    //发送消息
    override suspend fun sendMessage(targetId: String, content: String) {
        val messageDto = MessageDto(
            id = UUID.randomUUID().toString(),
            senderId = userId,
            targetId = targetId,
            timestamp = System.currentTimeMillis(),
            content = content
        )
        val messageEntity = MessageEntity(
            id = messageDto.id,
            ownerId = userId,
            conversationId = targetId,
            senderId = userId,
            targetId = targetId,
            isMe = true,
            timestamp = messageDto.timestamp,
            content = content
        )
        messageDao.insertMessage(messageEntity)
        conversationDao.updateLastMessage(targetId, content, messageDto.timestamp, userId)
        socketManager.sendMessage(messageDto)
    }

    //获取所有消息
    override fun observeMessages(conversationId: String): Flow<List<Message>> {
        return messageDao.observeMessages(conversationId, userId).map {
            it.map { it.toDomain() }
        }
    }

    //设置当前活跃conversationId
    override fun setActiveConversation(conversationId: String?) {
        this.activeConversationId = conversationId
    }

    override suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            //读取文件
            val inputStream = context.contentResolver.openInputStream(uri) ?: return Result.failure(
                IllegalArgumentException("无法读取文件")
            )

            val bytes = inputStream.use { it.readBytes() }

            //转换为RequestBody
            val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())

            //构建MultipartBody
            val multipartBody = MultipartBody.Part.createFormData(
                "image", "image_${System.currentTimeMillis()}.jpg", requestBody
            )

            val response = chatApi.uploadImage(multipartBody)

            if (response.code == 200 && !response.url.isNullOrBlank()) {
                Result.success(response.url)
            } else {
                Result.failure(Exception(response.message ?: "上传失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    //开启事件监听
    private fun observeSocketEvents() {
        scope.launch {
            socketManager.eventFlow.collect { event ->
                when (event) {
                    is SocketEvent.MessageReceived -> {
                        handleMessage(event.data)
                    }

                    else -> {}
                }
            }
        }
    }

    //处理接收到的消息
    private suspend fun handleMessage(dto: MessageDto) {

        // 确保联系人存在（没有就创建一条占位联系人，避免外键崩溃）
        var contact = contactDao.getContactById(dto.senderId, userId)
        if (contact == null) {
            contact = ContactEntity(
                id = dto.senderId,
                ownerId = userId,
                name = "新好友",
                avatar = "",
                phone = "",
                gender = Gender.UNKNOWN,
                remarks = null
            )
            contactDao.insertContact(contact)
        }

        //查看是否有对方的会话
        val existingConversation = conversationDao.getConversationById(dto.senderId, userId)

        if (existingConversation != null) {
            //判断当前是否在会话中
            val isCurrentActive = activeConversationId == dto.senderId
            //确保发送者不是自己
            if (isCurrentActive && dto.senderId != userId) {
                conversationDao.updateLastMessage(
                    dto.senderId, dto.content, dto.timestamp, userId
                )
            } else {
                conversationDao.updateLastMessage(
                    dto.senderId, dto.content, dto.timestamp, userId
                )
                conversationDao.updateUnreadCount(dto.senderId, userId)
            }
        } else {
            // 更新会话列表
            val conversation = ConversationEntity(
                id = dto.senderId, // 使用对方ID作为会话ID
                ownerId = userId,
                contactId = dto.senderId,
                lastMessage = dto.content,
                lastMessageTime = dto.timestamp,
                unreadCount = 1
            )
            conversationDao.insertConversation(conversation = conversation)
        }

        // 保存消息
        val messageEntity = MessageEntity(
            id = dto.id,
            ownerId = userId,
            conversationId = dto.senderId,
            senderId = dto.senderId,
            targetId = dto.targetId,
            isMe = false,
            content = dto.content,
            timestamp = dto.timestamp
        )
        messageDao.insertMessage(messageEntity)

        if (dto.senderId != userId && activeConversationId != dto.senderId) {
            val notificationMessage = NotificationMessage(
                messageId = dto.id,
                conversationId = dto.senderId,
                targetId = dto.senderId,
                targetName = contact.name,
                content = dto.content,
                timestamp = dto.timestamp
            )
            notificationDispatcher.showMessage(notificationMessage)
        }
    }
}