package com.example.chatapplication.data.repository.impl // 仓库实现层包名

import android.content.Context
import android.net.Uri
import com.example.chatapplication.data.local.dao.ContactDao
import com.example.chatapplication.data.mapper.toDomain // 实体转领域模型映射
import com.example.chatapplication.data.remote.retrofit.api.ProfileApi
import com.example.chatapplication.data.repository.ProfileRepository // 个人资料仓库接口
import com.example.chatapplication.data.repository.SessionRepository
import com.example.chatapplication.data.model.Contact
import com.example.chatapplication.data.model.Gender
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow // 异步流
import kotlinx.coroutines.flow.map // 流转换操作符
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject // Hilt 注入注解

class ProfileRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    private val profileApi: ProfileApi,
    private val sessionRepository: SessionRepository,
    @ApplicationContext private val context: Context
) : ProfileRepository {

    private var userId: String = ""
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun start() {
        scope.launch {
            userId = sessionRepository.getUserId()!!
        }
    }

    //获取我的资料
    override fun observeProfile(userId: String): Flow<Contact> {
        return contactDao.observeContact(userId, userId).map {
            it.toDomain()
        }
    }

    //更新我的资料
    override suspend fun updateProfile(
        id: String,
        name: String?,
        gender: Gender?,
        avatar: String?
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val textMediaType = "text/plain".toMediaType()
                val userIdBody = id.toRequestBody(textMediaType)
                val nameBody = name?.toRequestBody(textMediaType)
                val genderBody = gender?.name?.toRequestBody(textMediaType)
                var avatarPart: MultipartBody.Part? = null
                var finalAvatarPath = avatar

                if (avatar != null) {
                    val avatarUri = Uri.parse(avatar)
                    avatarPart =
                        context.contentResolver.openInputStream(avatarUri)?.use { inputStream ->
                            val bytes = inputStream.readBytes()

                            try {
                                val file = File(context.filesDir, "avatar_${id}.jpg")
                                file.writeBytes(bytes)
                                finalAvatarPath = file.absolutePath
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            val imageRequestBody = bytes.toRequestBody("image/jpeg".toMediaType())
                            MultipartBody.Part.createFormData(
                                name = "avatar",
                                filename = "avatar_${System.currentTimeMillis()}.jpg",
                                body = imageRequestBody
                            )
                        }
                }
                //获取服务器响应
                val response = profileApi.updateProfile(
                    userId = userIdBody,
                    name = nameBody,
                    gender = genderBody,
                    avatar = avatarPart
                )
                if (response.code == 200) {
                    if (name != null) {
                        contactDao.updateName(id, name, userId)
                    }
                    if (gender != null) {
                        contactDao.updateGender(id, gender.ordinal, userId)
                    }
                    if (avatar != null) {
                        contactDao.updateAvatar(id, finalAvatarPath!!, userId)
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
