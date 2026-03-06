package com.example.chatapplication.viewmodel // ViewModel 层包

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.RegisterRepository
import com.example.chatapplication.service.ChatService
import com.example.chatapplication.ui.screen.register.RegisterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onPhoneChange(phone: String) { // 用户输入手机号时调用
        _uiState.update { it.copy(phone = phone) }
    }

    fun onPasswordChange(passwordHash: String) { // 用户输入密码时调用
        _uiState.update { it.copy(passwordHash = passwordHash) }
    }

    fun onConfirmPasswordChange(confirmPasswordHash: String) { // 用户输入确认密码时调用
        _uiState.update { it.copy(confirmPasswordHash = confirmPasswordHash) }
    }

    fun register() {
        viewModelScope.launch { // 在 ViewModel 的协程作用域中启动异步操作
            val state = _uiState.value

            // 检查手机号或密码是否为空
            if (state.phone.isBlank() || state.passwordHash.isBlank()) {
                _uiState.update { it.copy(error = "手机号或密码不能为空") }
                return@launch // 提前退出
            }

            // 手机号格式校验
            if (!isValidPhone(state.phone)) {
                _uiState.update { it.copy(error = "请输入正确的手机号") }
                return@launch
            }

            // 密码格式校验：至少 6 位
            if (!isValidPassword(state.passwordHash)) {
                _uiState.update { it.copy(error = "密码至少6位") }
                return@launch
            }

            // 检查两次密码是否一致
            if (state.passwordHash != state.confirmPasswordHash) {
                _uiState.update { it.copy(error = "两次输入的密码不一致") }
                return@launch // 提前退出
            }

            // 设置加载中状态，同时清除错误信息
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = registerRepository.register(
                    state.phone, state.passwordHash
                ) // 调用 Repository 将用户保存到数据库
                if (response == true) {
                    _uiState.update {
                        it.copy(isLoading = false, registerSuccess = true)
                    } // 更新 UI 状态：注册成功，取消加载
                    //服务Intent
                    val serviceIntent = Intent(context, ChatService::class.java)
                    // 启动后台消息服务（保证后台能收到消息） 分安卓版本运行
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                }
            } catch (e: Exception) { // 捕获异常
                _uiState.update {
                    it.copy(
                        isLoading = false, error = e.message ?: "注册失败"
                    )
                } // 更新 UI 状态显示错误并取消加载
            }
        }
    }

    // 中国大陆常见手机号规则：1 + 第二位 3-9 + 后面 9 位数字
    private fun isValidPhone(phone: String): Boolean {
        val regex = Regex("^1[3-9]\\d{9}$")
        return regex.matches(phone)
    }

    // 至少 6 位
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}

