package com.example.chatapplication.data.repository.impl

import android.content.Context
import com.example.chatapplication.data.local.dao.ContactApplyDao
import com.example.chatapplication.data.local.dao.ContactDao
import com.example.chatapplication.data.local.dao.ConversationDao
import com.example.chatapplication.data.local.entity.ContactApplyEntity
import com.example.chatapplication.data.local.entity.ContactEntity
import com.example.chatapplication.data.local.entity.ConversationEntity
import com.example.chatapplication.data.mapper.toDomain
import com.example.chatapplication.data.mapper.toEntity
import com.example.chatapplication.data.remote.retrofit.api.ContactApplyApi
import com.example.chatapplication.data.remote.retrofit.request.contactApply.ApplyContactRequest
import com.example.chatapplication.data.remote.retrofit.request.contactApply.HandleContactRequest
import com.example.chatapplication.data.remote.socket.SocketEvent
import com.example.chatapplication.data.remote.socket.SocketManager
import com.example.chatapplication.data.remote.socket.dto.ContactAcceptedDto
import com.example.chatapplication.data.repository.ContactApplyRepository
import com.example.chatapplication.data.repository.SessionRepository
import com.example.chatapplication.data.model.Contact
import com.example.chatapplication.data.model.ContactApply
import com.example.chatapplication.data.model.Gender
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject

class ContactApplyRepositoryImpl @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val contactApplyDao: ContactApplyDao,
    private val conversationDao: ConversationDao,
    private val contactDao: ContactDao,
    private val contactApplyApi: ContactApplyApi,
    private val socketManager: SocketManager,
    @ApplicationContext private val context: Context
) : ContactApplyRepository {

    private var userId: String = ""
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun start() {
        scope.launch {
            userId = sessionRepository.getUserId()!!
            observeSocketEvents()
        }
    }

    //获取全部的申请
    override fun observeContactApplys(): Flow<List<ContactApply>> {
        return contactApplyDao.observeContactApplys(userId).map {
            it.map { it.toDomain() }
        }
    }

    //搜索联系人
    override suspend fun searchContact(phone: String): Contact? {
        val response = contactApplyApi.searchContact(phone, userId)
        //将获取到的联系人返回
        if (response.code == 200 && response.contact != null) {
            val contact = response.contact.toDomain()
            return contact
        }
        return null
    }

    //申请添加好友
    override suspend fun applyContact(contactId: String, message: String): Boolean {
        try {
            //向服务器发送申请
            val response =
                contactApplyApi.applyContact(ApplyContactRequest(userId, contactId, message))
            if (response.code == 200 && response.contactApplyResponse != null) {
                val contactApplyEntity = ContactApplyEntity(
                    applyId = response.contactApplyResponse.applyId,
                    contactId = response.contactApplyResponse.contactId,
                    ownerId = userId,
                    name = response.contactApplyResponse.name,
                    phone = response.contactApplyResponse.phone,
                    avatar = response.contactApplyResponse.avatar,
                    gender = Gender.fromString(response.contactApplyResponse.gender),
                    status = response.contactApplyResponse.status,
                    message = response.contactApplyResponse.message,
                    createdAt = response.contactApplyResponse.createdAt,
                    handledAt = response.contactApplyResponse.handledAt,
                    isMySent = true
                )
                contactApplyDao.insertContactApply(contactApplyEntity)
                return true
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    //拉取我接收的申请列表
    override suspend fun getMyReceivedContact() {
        val response = contactApplyApi.getMyReceivedContact(userId)
        //从服务器拿到我接收的申请列表并持久化
        if (response.code == 200 && response.list != null) {
            val contactApplies = response.list.map { contactApply ->
                val contactAvatarPath = saveAvatar(contactApply.contactId, contactApply.avatar)
                ContactApply(
                    contact = Contact(
                        id = contactApply.contactId,
                        name = contactApply.name,
                        avatar = contactAvatarPath,
                        phone = contactApply.phone,
                        gender = Gender.fromString(contactApply.gender)
                    ),
                    applyId = contactApply.applyId,
                    status = contactApply.status,
                    message = contactApply.message,
                    createdAt = contactApply.createdAt,
                    handledAt = contactApply.handledAt,
                    isMySent = false
                )
            }
            contactApplyDao.insertContactApplys(contactApplies.map { it.toEntity(userId) })
        }
    }

    //拉取我申请的申请列表
    override suspend fun getMySentContact() {
        val response = contactApplyApi.getMySentContact(userId)
        //从服务器拿到我申请的申请列表并持久化
        if (response.code == 200 && response.list != null) {
            val contactApplies = response.list.map { contactApply ->
                val contactAvatarPath = saveAvatar(contactApply.contactId, contactApply.avatar)
                ContactApply(
                    contact = Contact(
                        id = contactApply.contactId,
                        name = contactApply.name,
                        avatar = contactAvatarPath,
                        phone = contactApply.phone,
                        gender = Gender.fromString(contactApply.gender)
                    ),
                    applyId = contactApply.applyId,
                    status = contactApply.status,
                    message = contactApply.message,
                    createdAt = contactApply.createdAt,
                    handledAt = contactApply.handledAt,
                    isMySent = true
                )
            }
            contactApplyDao.insertContactApplys(contactApplies.map { it.toEntity(userId) })
        }
    }

    //处理我收到的申请
    override suspend fun handleContact(applyId: Int, action: String): Boolean {
        return try {
            val handleAt = System.currentTimeMillis()
            val response =
                contactApplyApi.handleContact(HandleContactRequest(applyId, action, handleAt))
            if (response.code == 200) {
                val contactApply = contactApplyDao.getContactApplyById(applyId, userId)
                contactApplyDao.insertContactApply(
                    contactApply!!.copy(
                        status = if (action == "accept") "accepted" else "rejected",
                        handledAt = handleAt
                    )
                )
                //添加好友到数据库的逻辑交给广播
                return true
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    //监听事件
    private fun observeSocketEvents() {
        scope.launch {
            socketManager.eventFlow.collect { event ->
                when (event) {
                    //好友同意申请后触发
                    is SocketEvent.ContactAccepted -> {
                        handleContactAccepted(event.data)
                    }

                    else -> {}
                }
            }
        }
    }

    //对方接收后进行处理
    private suspend fun handleContactAccepted(dto: ContactAcceptedDto) {
        val avatarPath = saveAvatar(dto.contactId, dto.avatar)!!
        val contactApply = ContactApplyEntity(
            applyId = dto.applyId,
            contactId = dto.contactId,
            ownerId = userId,
            name = dto.name,
            avatar = avatarPath,
            phone = dto.phone,
            gender = Gender.fromString(dto.gender),
            status = dto.status,
            message = dto.message,
            createdAt = dto.createdAt,
            handledAt = dto.handledAt,
            isMySent = dto.isMySent
        )
        val contact = ContactEntity(
            id = dto.contactId,
            ownerId = userId,
            name = dto.name,
            avatar = avatarPath,
            phone = dto.phone,
            gender = Gender.fromString(dto.gender),
            remarks = ""
        )
        contactDao.insertContact(contact)
        contactApplyDao.insertContactApply(contactApply)
        val conversationEntity = ConversationEntity(
            id = dto.contactId,
            ownerId = userId,
            contactId = dto.contactId,
            lastMessage = dto.message,
            lastMessageTime = dto.handledAt,
            unreadCount = 0
        )
        conversationDao.insertConversation(conversationEntity)
    }

    //头像持久化
    private suspend fun saveAvatar(userId: String, avatarUrl: String?): String? {
        // 如果不是远程 URL，直接返回原值
        if (avatarUrl.isNullOrEmpty() || !avatarUrl.startsWith("http")) {
            return avatarUrl
        }

        return withContext(Dispatchers.IO) {
            try {
                // 1. 从 URL 中提取文件名作为唯一标识（例如：1739352123456.jpg）
                val remoteFileName = avatarUrl.substringAfterLast("/")
                // 2. 构造包含版本标识的本地文件名
                val localFileName = "avatar_${userId}_$remoteFileName"
                val file = File(context.filesDir, localFileName)

                // ✅ 如果该版本的头像已存在，直接返回本地路径（无需重复下载）
                if (file.exists() && file.length() > 0) {
                    return@withContext file.absolutePath
                }

                // ❗ 发现新版本：清理该用户旧的头像文件，防止占用存储空间
                context.filesDir.listFiles { _, name ->
                    name.startsWith("avatar_$userId") && name != localFileName
                }?.forEach { it.delete() }

                // ❗ 下载并保存新头像
                val bytes = URL(avatarUrl).readBytes()
                file.writeBytes(bytes)

                // 返回新的路径字符串，这会触发 UI 层的重组和刷新
                file.absolutePath

            } catch (e: Exception) {
                avatarUrl // 失败时回退到 URL
            }
        }
    }

}