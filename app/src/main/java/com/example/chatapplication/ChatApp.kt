package com.example.chatapplication // 应用主包名

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // 触发 Hilt 代码生成，作为依赖注入的根容器
class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
