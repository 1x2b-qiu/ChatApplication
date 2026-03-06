package com.example.chatapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.chatapplication.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    //插入一个新的联系人
    @Upsert
    suspend fun insertContact(contact: ContactEntity)

    //批量插入新的联系人
    @Upsert
    suspend fun insertContacts(contacts: List<ContactEntity>)

    //查询全部联系人
    @Query("SELECT * FROM contacts WHERE ownerId = :ownerId ORDER BY name ASC ")
    fun observeContacts(ownerId: String): Flow<List<ContactEntity>>

    //查询单个联系人（状态流）
    @Query("SELECT * FROM contacts WHERE id = :id AND ownerId = :ownerId")
    fun observeContact(id: String, ownerId: String): Flow<ContactEntity>

    //查询单个联系人（非状态流）
    @Query("SELECT * FROM contacts WHERE id = :id AND ownerId = :ownerId LIMIT 1")
    suspend fun getContactById(id: String, ownerId: String): ContactEntity?

    // 通过手机号或昵称进行精确搜索（单参数匹配任一字段）
    @Query("SELECT * FROM contacts WHERE ownerId = :ownerId AND (name = :query OR phone = :query)")
    suspend fun searchContacts(query: String, ownerId: String): List<ContactEntity>

    //更新联系人姓名
    @Query("UPDATE contacts SET name = :name WHERE id = :id AND ownerId = :ownerId")
    suspend fun updateName(id: String, name: String, ownerId: String)

    //更新联系人头像
    @Query("UPDATE contacts SET avatar = :avatar WHERE id = :id AND ownerId = :ownerId")
    suspend fun updateAvatar(id: String, avatar: String, ownerId: String)

    //更新联系人电话
    @Query("UPDATE contacts SET phone = :phone WHERE id = :id AND ownerId = :ownerId")
    suspend fun updatePhone(id: String, phone: String, ownerId: String)

    //更新联系人性别
    @Query("UPDATE contacts SET gender = :genderValue WHERE id = :id AND ownerId = :ownerId")
    suspend fun updateGender(id: String, genderValue: Int, ownerId: String)

}
