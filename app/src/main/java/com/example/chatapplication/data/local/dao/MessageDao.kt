package com.example.chatapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.chatapplication.data.local.entity.MessageEntity
import com.example.chatapplication.data.local.relation.MessageWithSender
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    //插入一条消息
    @Upsert
    suspend fun insertMessage(message: MessageEntity)

    //插入很多条消息（消息同步）
    @Upsert
    suspend fun insertMessages(messages: List<MessageEntity>)

    //获取所有消息
    @Transaction    //在一个事务中执行 要么全部成功 要么全部失败 当使用 @Relation 时必须加 @Transaction
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND ownerId = :ownerId ORDER BY timestamp ASC")
    fun observeMessages(conversationId: String, ownerId: String): Flow<List<MessageWithSender>>

}