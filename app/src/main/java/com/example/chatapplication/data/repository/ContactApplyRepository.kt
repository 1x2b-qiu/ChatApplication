package com.example.chatapplication.data.repository

import com.example.chatapplication.data.model.Contact
import com.example.chatapplication.data.model.ContactApply
import kotlinx.coroutines.flow.Flow

interface ContactApplyRepository {
    fun start()

    //获取全部的申请
    fun observeContactApplys(): Flow<List<ContactApply>>

    //搜索联系人
    suspend fun searchContact(phone: String): Contact?

    //申请添加好友
    suspend fun applyContact(contactId: String, message: String): Boolean

    //拉取我接收的申请列表
    suspend fun getMyReceivedContact()

    //拉取我申请的申请列表
    suspend fun getMySentContact()

    //处理我收到的申请
    suspend fun handleContact(applyId: Int, action: String): Boolean
}