package com.example.chatapplication.ui.screen.chat // 聊天页面模型包名

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.ChatRepository
import com.example.chatapplication.data.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // 标记为 Hilt 管理的 ViewModel
class ChatViewModel @Inject constructor( // 支持注入的构造函数
    private val chatRepository: ChatRepository, // 注入聊天业务仓库
    private val conversationRepository: ConversationRepository, savedStateHandle: SavedStateHandle
) : ViewModel() { // 继承 ViewModel

    val conversationId: String = checkNotNull(savedStateHandle["conversationId"]) // 获取会话 ID
    val targetName: String = checkNotNull(savedStateHandle["targetName"]) // 获取目标名称
    val targetId: String = checkNotNull(savedStateHandle["targetId"]) // 获取目标 ID
    private val _uiState = MutableStateFlow(ChatUiState(targetName = targetName)) // 私有可变 UI 状态流
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow() // 对外暴露的只读 UI 状态流

    init {
        chatRepository.setActiveConversation(conversationId)
        markAsRead()
        observeMessages()
    }

    fun onInputTextChange(newText: String) {
        _uiState.update { it.copy(inputText = newText) }
    }

    //获取所有消息
    fun observeMessages() {
        try {
            viewModelScope.launch {
                chatRepository.observeMessages(conversationId).collect { messages ->
                    _uiState.update { it.copy(messages = messages) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //发送消息
    fun sendMessage() {
        try {
            viewModelScope.launch {
                chatRepository.sendMessage(targetId, _uiState.value.inputText)
                _uiState.update { it.copy(inputText = "") }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendImageMessage(uri: Uri) {
        viewModelScope.launch {
            val result = chatRepository.uploadImage(uri)
            result.getOrNull()?.let { uri ->
                chatRepository.sendMessage(targetId, uri)
            }
        }
    }


    // 标记已读
    private fun markAsRead() {
        viewModelScope.launch {
            conversationRepository.resetConversationUnreadCount(conversationId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatRepository.setActiveConversation(null)
    }

}
