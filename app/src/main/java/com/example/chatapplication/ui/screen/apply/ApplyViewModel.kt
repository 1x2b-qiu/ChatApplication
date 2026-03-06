package com.example.chatapplication.ui.screen.apply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.ContactApplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplyViewModel @Inject constructor(
    private val contactApplyRepository: ContactApplyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApplyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeContactApplys()
        syncApplies()
    }

    //拉取最新申请
    private fun syncApplies() {
        viewModelScope.launch {
            try {
                contactApplyRepository.getMyReceivedContact()
                contactApplyRepository.getMySentContact()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun observeContactApplys() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            contactApplyRepository.observeContactApplys().collect { allApplys ->
                // 1. 首先区分 待处理(pending) 和 已处理(others)
                val (pendingAll, others) = allApplys.partition { it.status == "pending" }
                // 2. 将待处理(pending)进一步拆分为：我收到的(received) 和 我发出的(sent)
                val (sentPending, receivedPending) = pendingAll.partition { it.isMySent }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        pendingApplys = receivedPending.sortedByDescending { item -> item.createdAt },
                        sentApplys = sentPending.sortedByDescending { item -> item.createdAt },
                        recentActivity = others.sortedByDescending { item -> item.createdAt },
                        error = null
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun searchContact() {
        viewModelScope.launch {
            if (_uiState.value.searchQuery.isBlank()) return@launch
            _uiState.update { it.copy(isLoading = true, hasSearched = true) }
            try {
                val result = contactApplyRepository.searchContact(_uiState.value.searchQuery)
                _uiState.update { it.copy(isLoading = false, searchResult = result) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, searchResult = null) }
            }
        }
    }

    fun applyContact() {
        viewModelScope.launch {
            try {
                if (_uiState.value.searchResult != null) {
                    contactApplyRepository.applyContact(
                        _uiState.value.searchResult!!.id, _uiState.value.message
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleContact(applyId: Int, action: String) {
        viewModelScope.launch {
            try {
                contactApplyRepository.handleContact(applyId, action)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearSearchResult() {
        _uiState.update { it.copy(hasSearched = false, searchResult = null) }
    }
}
