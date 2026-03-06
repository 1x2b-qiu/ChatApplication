package com.example.chatapplication.ui.screen.profile.name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.ProfileRepository
import com.example.chatapplication.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NameViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NameUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCurrentName()
    }

    private fun loadCurrentName() {
        viewModelScope.launch {
            val userId = sessionRepository.observeUserId().first()!!
            profileRepository.observeProfile(userId = userId).collect { contact ->
                _uiState.update {
                    it.copy(name = contact.name)
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun updateName() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = sessionRepository.observeUserId().first()!!
            val response = profileRepository.updateProfile(
                id = userId,
                name = _uiState.value.name
            )
            if (response == true) {
                _uiState.update {
                    it.copy(isSuccess = true, isLoading = false)
                }
            } else {
                _uiState.update {
                    it.copy(error = "更新昵称失败", isLoading = false)
                }
            }
        }
    }

}