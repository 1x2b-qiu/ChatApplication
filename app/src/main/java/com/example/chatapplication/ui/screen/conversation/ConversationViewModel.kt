package com.example.chatapplication.ui.screen.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.ConversationRepository
import com.example.chatapplication.data.model.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState

    init {
        observeConversations()
    }

    //获取全部会话列表
    fun observeConversations() {
        try {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                conversationRepository.observeConversations().collect { list ->
                    _uiState.update { it.copy(conversations = list, isLoading = false) }
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, isLoading = false) }
        }
    }

    //删除会话
    fun deleteConversation(id: String) {
        try {
            viewModelScope.launch { conversationRepository.deleteConversation(id) }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    //切换置顶状态
    fun toggleConversationIsPinned(conv: Conversation) = viewModelScope.launch {
        conversationRepository.updateConversationIsPinned(conv.id, !conv.isPinned)
    }
}
