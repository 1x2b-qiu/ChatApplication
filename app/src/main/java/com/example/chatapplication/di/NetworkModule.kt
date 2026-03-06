package com.example.chatapplication.di // 映射包名

import com.example.chatapplication.data.remote.retrofit.api.AuthApi
import com.example.chatapplication.data.remote.retrofit.api.ChatApi
import com.example.chatapplication.data.remote.retrofit.api.ContactApi
import com.example.chatapplication.data.remote.retrofit.api.ContactApplyApi
import com.example.chatapplication.data.remote.retrofit.api.ProfileApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module // 标记为 Hilt 模块
@InstallIn(SingletonComponent::class) // 安装到应用级生命周期
object NetworkModule { // 网络依赖提供者

    @Provides // 提供 Retrofit 实例
    @Singleton // 全局单例
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://121.43.25.186:8080/") // ！！！修改为你的 IP 地址，注意：通常需要端口号（如 8080）
            .client(okHttpClient) // 使用自定义 OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // 自动 JSON 解析
            .build()
    }

    @Provides // 提供登录服务接口
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }


    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }


    @Provides
    @Singleton
    fun provideContactApi(retrofit: Retrofit): ContactApi {
        return retrofit.create(ContactApi::class.java)
    }

    @Provides
    @Singleton
    fun provideContactApplyApi(retrofit: Retrofit): ContactApplyApi {
        return retrofit.create(ContactApplyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi {
        return retrofit.create(ChatApi::class.java)
    }


    @Provides // 提供 OkHttpClient 实例
    @Singleton // 全局单例
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS) // 连接超时
            .readTimeout(15, TimeUnit.SECONDS) // 读取超时
            .writeTimeout(5, TimeUnit.MINUTES)  //写入超时
            .pingInterval(10, TimeUnit.SECONDS) // WebSocket 心跳（预留）
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

}
