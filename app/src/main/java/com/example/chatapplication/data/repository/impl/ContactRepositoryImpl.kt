package com.example.chatapplication.data.repository.impl

import android.content.Context
import com.example.chatapplication.data.local.dao.ContactDao
import com.example.chatapplication.data.mapper.toDomain
import com.example.chatapplication.data.mapper.toEntity
import com.example.chatapplication.data.remote.retrofit.api.ContactApi
import com.example.chatapplication.data.remote.socket.SocketEvent
import com.example.chatapplication.data.remote.socket.SocketManager
import com.example.chatapplication.data.remote.socket.dto.ContactProfileUpdatedDto
import com.example.chatapplication.data.repository.ContactRepository
import com.example.chatapplication.data.repository.SessionRepository
import com.example.chatapplication.data.model.Contact
import com.example.chatapplication.data.model.Gender
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    private val sessionRepository: SessionRepository,
    @ApplicationContext private val context: Context,
    private val socketManager: SocketManager,
    private val contactApi: ContactApi
) : ContactRepository {

    private var userId: String = ""
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun start() {
        scope.launch {
            userId = sessionRepository.getUserId()!!
            observeSocketEvents()
        }
    }

    //查看所有联系人
    override fun observeContacts(): Flow<List<Contact>> {
        return contactDao.observeContacts(userId).map {
            it.map { it.toDomain() }
        }
    }

    //拿到单个联系人信息
    override fun observeContact(id: String): Flow<Contact> {
        return contactDao.observeContact(id, userId).map { it.toDomain() }
    }

    //搜索我的好友
    override suspend fun searchContacts(query: String): List<Contact> {
        return contactDao.searchContacts(query, userId).map { it.toDomain() }
    }

    //监听事件
    private fun observeSocketEvents() {
        scope.launch {
            socketManager.eventFlow.collect { event ->
                when (event) {
                    //连接后触发
                    is SocketEvent.Connected -> {
                        refreshContacts()
                    }
                    //好友更新资料后触发
                    is SocketEvent.ContactProfileUpdated -> {
                        handleContactProfileUpdated(event.data)
                    }

                    else -> {}
                }
            }
        }
    }

    //刷新联系人列表
    private suspend fun refreshContacts() {
        println("refreshContacts")
        val userId = sessionRepository.observeUserId().first() ?: ""
        try {
            val response = contactApi.getContacts(userId)
            if (response.code == 200 && response.contacts != null) {
                val newContacts = response.contacts.map { contact ->
                    val avatarPath = saveAvatar(contact.id, contact.avatar) ?: ""
                    contact.copy(avatar = avatarPath)
                }
                contactDao.insertContacts(newContacts.map { it.toDomain().toEntity(userId) })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //处理联系人资料更新
    private suspend fun handleContactProfileUpdated(dto: ContactProfileUpdatedDto) {
        if (dto.name != null) {
            contactDao.updateName(dto.userId, dto.name, userId)
        }
        if (dto.avatar != null) {
            val avatarPath = saveAvatar(dto.userId, dto.avatar)
            contactDao.updateAvatar(dto.userId, avatarPath!!, userId)
        }
        if (dto.gender != null) {
            val genderEnum = Gender.fromString(dto.gender)
            contactDao.updateGender(dto.userId, genderEnum.ordinal, userId)
        }
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