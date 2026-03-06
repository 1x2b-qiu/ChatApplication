package com.example.chatapplication.di // Hilt 依赖注入模块所在包（仓库层相关）

import com.example.chatapplication.data.repository.ChatRepository
import com.example.chatapplication.data.repository.ContactApplyRepository
import com.example.chatapplication.data.repository.ContactRepository
import com.example.chatapplication.data.repository.ConversationRepository
import com.example.chatapplication.data.repository.LoginRepository
import com.example.chatapplication.data.repository.ProfileRepository // 个人资料业务接口
import com.example.chatapplication.data.repository.RegisterRepository // 注册业务接口
import com.example.chatapplication.data.repository.SessionRepository // 用户会话业务接口
import com.example.chatapplication.data.repository.impl.ChatRepositoryImpl
import com.example.chatapplication.data.repository.impl.ContactApplyRepositoryImpl
import com.example.chatapplication.data.repository.impl.ContactRepositoryImpl
import com.example.chatapplication.data.repository.impl.ConversationRepositoryImpl
import com.example.chatapplication.data.repository.impl.LoginRepositoryImpl
import com.example.chatapplication.data.repository.impl.ProfileRepositoryImpl // 个人资料业务实现类
import com.example.chatapplication.data.repository.impl.RegisterRepositoryImpl // 注册业务实现类
import com.example.chatapplication.data.repository.impl.SessionRepositoryImpl // 用户会话业务实现类
import dagger.Binds // Hilt 接口绑定注解，用于将抽象方法绑定到具体实现类
import dagger.Module // Hilt Module 注解，标记该类为依赖提供模块
import dagger.hilt.InstallIn // 指定 Module 安装到哪个 Hilt 组件
import dagger.hilt.components.SingletonComponent // Hilt 单例组件（应用级）
import javax.inject.Singleton // 标记依赖为单例

@Module // 声明这是一个 Hilt 模块
@InstallIn(SingletonComponent::class) // 安装到应用级生命周期
abstract class RepositoryModule { // Repository 相关依赖绑定抽象类

    @Binds // 将具体实现类绑定到其接口
    @Singleton // 标记为单例
    abstract fun bindRegisterRepository( // 绑定注册仓库
        registerRepositoryImpl: RegisterRepositoryImpl // 具体实现
    ): RegisterRepository // 接口类型

    @Binds // 将具体实现类绑定到其接口
    @Singleton // 标记为单例
    abstract fun bindProfileRepository( // 绑定个人资料仓库
        profileRepositoryImpl: ProfileRepositoryImpl // 具体实现
    ): ProfileRepository // 接口类型

    @Binds // 将具体实现类绑定到其接口
    @Singleton // 标记为单例
    abstract fun bindSessionRepository( // 绑定会话仓库
        sessionRepositoryImpl: SessionRepositoryImpl // 具体实现
    ): SessionRepository // 接口类型

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindConversationRepository(
        conversationRepositoryImpl: ConversationRepositoryImpl
    ): ConversationRepository


    @Binds
    @Singleton
    abstract fun bindContactRepository(
        contactRepositoryImpl: ContactRepositoryImpl
    ): ContactRepository

    @Binds
    @Singleton
    abstract fun bindContactApplyRepository(
        contactApplyRepositoryImpl: ContactApplyRepositoryImpl
    ): ContactApplyRepository

}
