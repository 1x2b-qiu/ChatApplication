package com.example.chatapplication.di // Hilt 依赖注入模块所在包（DataStore 相关）

import android.content.Context // Android 上下文，用于创建 DataStore 文件
import androidx.datastore.core.DataStore // DataStore 核心接口
import androidx.datastore.preferences.core.PreferenceDataStoreFactory // 用于创建 Preferences 类型的 DataStore
import androidx.datastore.preferences.core.Preferences // Preferences 数据类型
import androidx.datastore.preferences.preferencesDataStoreFile // 生成 DataStore 文件路径
import dagger.Module // Hilt Module 注解，标记该类为依赖提供模块
import dagger.Provides // Hilt 提供依赖的方法注解
import dagger.hilt.InstallIn // 指定 Module 安装到哪个 Hilt 组件
import dagger.hilt.android.qualifiers.ApplicationContext // 区分 Application 级 Context
import dagger.hilt.components.SingletonComponent // Hilt 单例组件（应用级）
import kotlinx.coroutines.CoroutineScope // 协程作用域
import kotlinx.coroutines.Dispatchers // 协程调度器
import kotlinx.coroutines.SupervisorJob // 协程作业管理，确保一个子协程失败不影响其他协程
import javax.inject.Singleton // 标记依赖为单例

@Module // 声明这是一个 Hilt 模块
@InstallIn(SingletonComponent::class) // 安装到应用级生命周期
object DataStoreModule { // 提供 DataStore 相关依赖的单例对象

    @Provides // 告诉 Hilt 这个方法可以提供依赖
    @Singleton // 整个应用只创建一个 DataStore 实例
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> { // 提供 Preferences 类型的 DataStore
        return PreferenceDataStoreFactory.create( // 使用工厂方法创建 DataStore
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()), // 指定协程作用域，IO 线程 + SupervisorJob
            produceFile = { context.preferencesDataStoreFile("session") } // 指定 DataStore 文件名和存放位置
        )
    }
}
