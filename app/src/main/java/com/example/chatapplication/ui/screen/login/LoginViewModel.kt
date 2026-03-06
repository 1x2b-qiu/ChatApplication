package com.example.chatapplication.ui.screen.login

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.LoginRepository
import com.example.chatapplication.service.ChatService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onPasswordHashChange(passwordHash: String) {
        _uiState.update { it.copy(passwordHash = passwordHash) }
    }

    fun login() {
        try {
            val state = _uiState.value // 获取当前 UI 状态
            _uiState.update { it.copy(isLoading = true) }
            // 校验输入是否为空
            if (state.phone.isBlank() || state.passwordHash.isBlank()) {
                _uiState.update { it.copy(error = "手机号或密码不能为空", isLoading = false) }
                return
            } else {
                viewModelScope.launch {
                    val response = loginRepository.login(state.phone, state.passwordHash)
                    if (response == true) {
                        _uiState.update { it.copy(loginSuccess = true, isLoading = false) }
                        //服务Intent
                        val serviceIntent = Intent(context, ChatService::class.java)
                        // 启动后台消息服务（保证后台能收到消息） 分安卓版本运行
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent)
                        } else {
                            context.startService(serviceIntent)
                        }
                    } else {
                        _uiState.update { it.copy(error = "手机号或密码错误", isLoading = false) }
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message ?: "登录失败", isLoading = false) }
        }
    }
}