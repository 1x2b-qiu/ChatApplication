package com.example.chatapplication.ui.screen.profile.main // 个人资料页面 UI 状态包名

import com.example.chatapplication.data.model.Contact

data class ProfileUiState ( // 个人资料页面 UI 状态类
    val contact: Contact? = null, // 联系人信息，默认为 null
    val isLoading: Boolean = false, // 是否正在加载数据，默认为 true
    val error: String? = null // 错误信息，默认为空
)
