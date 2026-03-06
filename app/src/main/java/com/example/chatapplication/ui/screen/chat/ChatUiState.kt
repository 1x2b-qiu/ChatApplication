package com.example.chatapplication.ui.screen.chat // 聊天页面 UI 状态包名

import com.example.chatapplication.data.model.Message // 导入消息领域模型

data class ChatUiState( // 聊天页面 UI 状态类
    val messages: List<Message> = emptyList(), // 消息列表，默认为空
    val targetName: String = "",
    val inputText: String = "", // 输入框当前的文本内容
    val isLoading: Boolean = false, // 是否正在加载历史消息
    val error: String? = null
)
