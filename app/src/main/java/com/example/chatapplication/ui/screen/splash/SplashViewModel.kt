package com.example.chatapplication.ui.screen.splash // 闪屏页面 ViewModel 包名

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.SessionRepository
import com.example.chatapplication.service.ChatService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // 标记为 Hilt 管理的 ViewModel
class SplashViewModel @Inject constructor(
    // 支持注入的构造函数
    private val sessionRepository: SessionRepository, // 注入会话仓库
    @ApplicationContext private val context: Context // 注入应用上下文
) : ViewModel() { // 继承 ViewModel

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkLoginAndNavigate()
    }

    //检查是否登录并导航
    private fun checkLoginAndNavigate() {
        viewModelScope.launch {
            val userId = sessionRepository.getUserId()
            if (userId != null) {
                _uiState.update { it.copy(isLoggedIn = true) }
                //服务Intent
                val serviceIntent = Intent(context, ChatService::class.java)
                // 启动后台消息服务（保证后台能收到消息） 分安卓版本运行
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}
