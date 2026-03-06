package com.example.chatapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.chatapplication.data.local.entity.ConversationEntity
import com.example.chatapplication.data.local.relation.ConversationWithContact
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    //插入一条会话
    @Upsert
    suspend fun insertConversation(conversation: ConversationEntity)

    //删除一条会话
    @Query("DELETE FROM conversations WHERE id = :id AND ownerId = :ownerId")
    suspend fun deleteConversation(id: String, ownerId: String)

    //获取全部会话列表
    @Transaction
    @Query("SELECT * FROM conversations WHERE ownerId = :ownerId ORDER BY isPinned DESC , lastMessageTime DESC")
    fun observeConversations(ownerId: String): Flow<List<ConversationWithContact>>

    //设置是否置顶
    @Query("UPDATE conversations SET isPinned = :isPinned WHERE id = :id AND ownerId = :ownerId")
    suspend fun updateIsPinned(id: String, isPinned: Boolean, ownerId: String)

    //查询指定对话
    @Query("SELECT * FROM conversations WHERE id = :id AND ownerId = :ownerId")
    suspend fun getConversationById(id: String, ownerId: String): ConversationEntity?

    //更新最新消息
    @Query("UPDATE conversations SET lastMessage = :lastMessage, lastMessageTime = :lastMessageTime WHERE id = :id AND ownerId = :ownerId")
    suspend fun updateLastMessage(
        id: String, lastMessage: String, lastMessageTime: Long, ownerId: String
    )

    //收到他人消息，但未进入聊天页面时，未读数自增
    @Query("UPDATE conversations SET unreadCount = unreadCount + 1 WHERE id = :id AND ownerId = :ownerId")
    suspend fun updateUnreadCount(id: String, ownerId: String)

    //进入聊天页面时，未读数置0
    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :id AND ownerId = :ownerId")
    suspend fun resetUnreadCount(id: String, ownerId: String)

}
