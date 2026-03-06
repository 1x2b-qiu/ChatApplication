package com.example.chatapplication.ui.screen.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeContacts()
    }

    fun onSearchQueryChange(query: String) {
        // 💡 关键点：当内容变化时，立即清空旧的搜索结果
        _uiState.update {
            it.copy(
                searchQuery = query, searchResults = emptyList()
            )
        }
    }

    fun observeContacts() {
        viewModelScope.launch {
            contactRepository.observeContacts().collect { contacts ->
                _uiState.update { it.copy(contacts = contacts, isLoading = false) }
            }
        }
    }

    fun searchContacts() {
        viewModelScope.launch {
            if (uiState.value.searchQuery.isBlank()) return@launch
            _uiState.update { it.copy(isLoading = true, hasSearched = true) }
            try {
                val contacts = contactRepository.searchContacts(query = uiState.value.searchQuery)
                if (contacts.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, searchResults = null) }
                } else {
                    _uiState.update { it.copy(isLoading = false, searchResults = contacts) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, searchResults = null) }
            }
        }
    }

    // 💡 供界面在进入或离开时调用，重置所有搜索相关状态
    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                hasSearched = false,
                isLoading = false
            )
        }
    }
}
