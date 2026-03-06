package com.example.chatapplication.data.repository

import com.example.chatapplication.data.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun start()

    //获取所有联系人
    fun observeContacts(): Flow<List<Contact>>

    //搜索我的好友
    suspend fun searchContacts(query: String): List<Contact>

    //获取单个联系人信息
    fun observeContact(id: String): Flow<Contact>
}
