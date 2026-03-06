package com.example.chatapplication.ui.screen.register // 注册页面 UI 状态包名

data class RegisterUiState( // 注册页面 UI 状态数据类
    val phone: String = "", // 手机号输入值
    val passwordHash: String = "", // 密码输入值
    val confirmPasswordHash: String = "", // 确认密码输入值
    val isLoading: Boolean = false, // 是否处于注册提交中
    val registerSuccess: Boolean = false, // 注册是否成功标识
    val error: String? = null // 注册过程中的错误提示信息
)
