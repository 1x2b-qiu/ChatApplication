package com.example.chatapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.chatapplication.data.local.entity.ContactApplyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactApplyDao {
    //插入一条好友申请记录
    @Upsert
    suspend fun insertContactApply(contactApply: ContactApplyEntity)

    //插入多条好友申请记录
    @Upsert
    suspend fun insertContactApplys(contactApplies: List<ContactApplyEntity>)

    //根据申请ID查询申请记录
    @Query("SELECT * FROM contact_apply WHERE applyId = :applyId AND ownerId = :ownerId")
    suspend fun getContactApplyById(applyId: Int, ownerId: String): ContactApplyEntity?

    //获取所有申请
    @Query("SELECT * FROM contact_apply WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun observeContactApplys(ownerId: String): Flow<List<ContactApplyEntity>>

}