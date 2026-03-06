package com.example.application.di // Hilt 依赖注入模块所在包（Database 相关）

import android.content.Context // Android 上下文，用于创建数据库
import androidx.room.Room // Room 数据库构建工具类
import com.example.chatapplication.data.local.dao.ContactApplyDao
import com.example.chatapplication.data.local.dao.ContactDao
import com.example.chatapplication.data.local.dao.ConversationDao
import com.example.chatapplication.data.local.dao.MessageDao
import com.example.chatapplication.data.local.dao.UserDao // User 表对应的 DAO 接口
import com.example.chatapplication.data.local.database.AppDatabase // Room 数据库抽象类
import dagger.Module // Hilt Module 注解，标记该类为依赖提供模块
import dagger.Provides // Hilt 提供依赖的方法注解
import dagger.hilt.InstallIn // 指定该 Module 安装到哪个 Hilt 组件中
import dagger.hilt.android.qualifiers.ApplicationContext // 区分 Application 级 Context
import dagger.hilt.components.SingletonComponent // Hilt 的单例组件（应用级）
import javax.inject.Singleton // 标记依赖为单例

@Module // 声明这是一个 Hilt 模块
@InstallIn(SingletonComponent::class) // 将该模块安装到应用级生命周期中
object DatabaseModule { // Database 相关依赖的集中提供类（单例对象）

    @Provides // 告诉 Hilt 这个方法可以提供一个依赖
    @Singleton // 确保整个应用中只创建一个 AppDatabase 实例
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase { // 提供 AppDatabase 实例
        return Room.databaseBuilder( // 使用 Room 的数据库构建器
            context, // 使用 Application Context，避免内存泄漏
            AppDatabase::class.java, // 数据库的抽象类
            "app_database.db" // 数据库文件名
        )
            .fallbackToDestructiveMigration() // 数据库版本升级失败时直接清空重建（开发阶段常用）
            .build() // 构建并返回 AppDatabase 实例
    }

    @Provides // 告诉 Hilt 这个方法可以提供 UserDao
    fun provideUserDao(database: AppDatabase): UserDao { // 通过 AppDatabase 提供 UserDao
        return database.userDao() // 调用数据库中的 userDao() 方法获取 DAO
    }

    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    fun provideConversationDao(database: AppDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    fun provideContactDao(database: AppDatabase): ContactDao {
        return database.contactDao()
    }

    @Provides
    fun provideContactApplyDao(database: AppDatabase): ContactApplyDao {
        return database.contactApplyDao()
    }
}
