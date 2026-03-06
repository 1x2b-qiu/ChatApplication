package com.example.chatapplication.ui.screen.login

data class LoginUiState(
    val phone: String = "", //电话
    val passwordHash: String = "",//登录密码
    val error: String? = null, // 错误信息，默认为空
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false
)

