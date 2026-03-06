package com.example.chatapplication.ui.screen.contactProfile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.ContactRepository
import com.example.chatapplication.data.repository.ConversationRepository
import com.example.chatapplication.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactProfileViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val conversationRepository: ConversationRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contactId: String = checkNotNull(savedStateHandle["contactId"])
    private val _uiState = MutableStateFlow(ContactProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getContactProfile()
    }

    //获取联系人信息
    private fun getContactProfile() {
        viewModelScope.launch {
            val userId = sessionRepository.getUserId()
            if (userId == contactId) {
                _uiState.update { it.copy(isMyProfile = true) }
            }
            contactRepository.observeContact(contactId).collect { contact ->
                _uiState.update { it.copy(contact = contact) }
            }
        }
    }

    //无会话时创建
    fun createConversation() {
        viewModelScope.launch {
            conversationRepository.createConversation(contactId)
        }
    }
}