package com.example.chatapplication.ui.screen.mine

import com.example.chatapplication.data.model.Contact

data class MineUiState (
    val contact: Contact? = null, // 联系人信息，默认为 null
    val isLoading: Boolean = false, // 是否正在加载数据，默认为 true
    val error: String? = null // 错误信息，默认为空
)