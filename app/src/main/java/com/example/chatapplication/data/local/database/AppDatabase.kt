package com.example.chatapplication.data.local.database // 数据库配置包名

import androidx.room.Database // Room 数据库注解
import androidx.room.RoomDatabase // Room 数据库基类
import com.example.chatapplication.data.local.dao.ContactApplyDao
import com.example.chatapplication.data.local.dao.ContactDao
import com.example.chatapplication.data.local.dao.ConversationDao
import com.example.chatapplication.data.local.dao.MessageDao
import com.example.chatapplication.data.local.dao.UserDao // 用户数据访问对象
import com.example.chatapplication.data.local.entity.ContactApplyEntity
import com.example.chatapplication.data.local.entity.ConversationEntity
import com.example.chatapplication.data.local.entity.MessageEntity
import com.example.chatapplication.data.local.entity.UserEntity // 用户实体类
import com.example.chatapplication.data.local.entity.ContactEntity


@Database( // 声明数据库配置
    entities = [UserEntity::class, MessageEntity::class, ConversationEntity::class, ContactEntity::class, ContactApplyEntity::class], // 包含的实体类列表
    version = 10,// 数据库版本号
    exportSchema = false//导出数据库结构
)
abstract class AppDatabase : RoomDatabase() { // 数据库抽象类
    abstract fun userDao(): UserDao // 获取用户操作接口
    abstract fun messageDao(): MessageDao // 获取消息操作接口
    abstract fun conversationDao(): ConversationDao // 获取会话操作接口
    abstract fun contactDao(): ContactDao // 获取联系人操作接口
    abstract fun contactApplyDao(): ContactApplyDao // 获取联系人申请操作接口
}
