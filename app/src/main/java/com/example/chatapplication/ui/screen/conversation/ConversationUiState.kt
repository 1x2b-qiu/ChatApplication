package com.example.chatapplication.ui.screen.conversation

import com.example.chatapplication.data.model.Conversation

data class ConversationUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)


