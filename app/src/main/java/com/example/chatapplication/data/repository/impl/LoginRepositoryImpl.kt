package com.example.chatapplication.data.repository.impl

import android.content.Context
import android.util.Log
import com.example.chatapplication.data.local.dao.ContactDao
import com.example.chatapplication.data.local.dao.UserDao
import com.example.chatapplication.data.local.entity.ContactEntity
import com.example.chatapplication.data.local.entity.UserEntity
import com.example.chatapplication.data.remote.retrofit.api.AuthApi
import com.example.chatapplication.data.remote.retrofit.request.auth.LoginRequest
import com.example.chatapplication.data.repository.LoginRepository
import com.example.chatapplication.data.repository.SessionRepository
import com.example.chatapplication.data.model.Gender
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionRepository: SessionRepository,
    private val userDao: UserDao,
    private val contactDao: ContactDao,
    @ApplicationContext private val context: Context
) : LoginRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    //登录
    override suspend fun login(phone: String, passwordHash: String): Boolean {
        return try {
            val response = authApi.login(LoginRequest(phone, passwordHash))
            if (response.code == 200) {
                //登录成功后进行持久化
                val userId = response.userId
                sessionRepository.login(response.userId!!)
                var myAvatarPath = saveAvatar(userId, response.avatar)

                val userEntity = UserEntity(
                    id = response.userId,
                    passwordHash = passwordHash,
                    lastLoginTime = response.lastLoginTime!!,
                    isActive = true
                )
                val contactEntity = ContactEntity(
                    id = response.userId,
                    ownerId = response.userId,
                    name = response.name!!,
                    avatar = myAvatarPath,
                    phone = phone,
                    gender = Gender.fromString(response.gender!!),
                    remarks = "isMe"
                )
                userDao.insertUser(userEntity)
                contactDao.insertContact(contactEntity)
                //持久化联系人列表
                val contacts = response.contacts ?: emptyList()
                if (contacts.isNotEmpty()) {
                    scope.launch {
                        try {
                            val contactEntites = contacts.map { contact ->
                                async {
                                    val contactAvatarPath = saveAvatar(contact.id, contact.avatar)
                                    ContactEntity(
                                        id = contact.id,
                                        ownerId = userId,
                                        name = contact.name,
                                        avatar = contactAvatarPath,
                                        phone = contact.phone,
                                        gender = Gender.fromString(contact.gender),
                                        remarks = ""
                                    )
                                }
                            }.awaitAll()
                            contactDao.insertContacts(contactEntites)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
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
                Log.e("ContactRepo", "头像下载失败: $userId", e)
                avatarUrl // 失败时回退到 URL
            }
        }
    }

}