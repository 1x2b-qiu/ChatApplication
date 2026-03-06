package com.example.chatapplication.data.repository.impl // 仓库实现层包名

import android.content.Context
import android.net.Uri
import com.example.chatapplication.data.local.dao.ContactDao
import com.example.chatapplication.data.local.dao.UserDao // 用户 DAO 接口
import com.example.chatapplication.data.local.entity.ContactEntity
import com.example.chatapplication.data.local.entity.UserEntity
import com.example.chatapplication.data.remote.retrofit.api.AuthApi
import com.example.chatapplication.data.remote.retrofit.request.auth.RegisterRequest
import com.example.chatapplication.data.remote.retrofit.response.auth.RegisterResponse
import com.example.chatapplication.data.repository.RegisterRepository // 注册仓库接口
import com.example.chatapplication.data.repository.SessionRepository
import com.example.chatapplication.data.model.Gender
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject // Hilt 注入注解

class RegisterRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val contactDao: ContactDao,
    private val sessionRepository: SessionRepository,
    private val authApi: AuthApi,
    @ApplicationContext private val context: Context
) : RegisterRepository {

    private var userId: String = ""
    private var lastLoginTime: Long = 0
    private var phone: String = ""
    private var passwordHash: String = ""

    //注册新用户
    override suspend fun register(phone: String, passwordHash: String): Boolean {
        val response: RegisterResponse = authApi.register(RegisterRequest(phone, passwordHash))
        //如果注册成功，把id，登录时间，电话，密码都返回，用于存储本地用户信息
        if (response.code == 200) {
            if (response.userId != null && response.lastLoginTime != null) {
                userId = response.userId
                lastLoginTime = response.lastLoginTime
                this.phone = phone
                this.passwordHash = passwordHash
                sessionRepository.login(userId)
                return true
            }
            return false
        } else {
            throw Exception(response.message ?: "服务器返回错误") // 抛出错误供 ViewModel 捕获
        }
    }

    //提交个人信息
    override suspend fun completeProfile(
        name: String, gender: Gender, avatar: String
    ): Boolean {
        return try {
            //请求体的格式
            val textMediaType = "text/plain".toMediaType()
            //请求体
            val userIdBody = userId.toRequestBody(textMediaType)
            val nameBody = name.toRequestBody(textMediaType)
            val genderBody = gender.name.toRequestBody(textMediaType)
            //把头像的临时路径解析成Uri
            val avatarUri = Uri.parse(avatar)
            val localFile = File(context.filesDir, "avatar_${userId}.jpg")

            //创建头像体，用于上传，contentResolver用于访问本地资源
            context.contentResolver.openInputStream(avatarUri)?.use { inputStream ->
                FileOutputStream(localFile).use { outputStream ->
                    inputStream.copyTo(outputStream) // 核心：流式拷贝
                }
            }

            // 💡 步骤 2：使用 OkHttp 的 asRequestBody 实现流式上传
            // 它会自动处理 writeTo 逻辑，确保不会将文件全部读入内存
            val imageRequestBody = localFile.asRequestBody("image/jpeg".toMediaType())
            val avatarPart = MultipartBody.Part.createFormData(
                name = "avatar",
                filename = "avatar_${System.currentTimeMillis()}.jpg",
                body = imageRequestBody
            )

            //提交资料，等待服务器返回
            val response = authApi.completeProfile(
                userId = userIdBody, name = nameBody, gender = genderBody, avatar = avatarPart
            )
            //返回成功，插入用户资料到本地数据库
            if (response.code == 200) {
                val userEntity = UserEntity(
                    id = userId,
                    passwordHash = passwordHash,
                    lastLoginTime = lastLoginTime,
                    isActive = true
                )
                val contactEntity = ContactEntity(
                    id = userId,
                    ownerId = userId,
                    name = name,
                    avatar = localFile.absolutePath,
                    phone = phone,
                    gender = gender,
                    remarks = "isMe"
                )
                userDao.insertUser(userEntity)
                contactDao.insertContact(contactEntity)
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
