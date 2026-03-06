package com.example.chatapplication.ui.screen.apply

import com.example.chatapplication.data.model.Contact
import com.example.chatapplication.data.model.ContactApply

data class ApplyUiState(
    val searchQuery: String = "",   //搜索
    val searchResult: Contact? = null,  //搜索结果
    val message: String = "",   //申请信息
    val hasSearched: Boolean = false,
    val pendingApplys: List<ContactApply> = emptyList(),
    val sentApplys: List<ContactApply> = emptyList(),
    val recentActivity: List<ContactApply> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
