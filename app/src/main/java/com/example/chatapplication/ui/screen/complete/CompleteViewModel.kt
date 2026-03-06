package com.example.chatapplication.ui.screen.complete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.RegisterRepository
import com.example.chatapplication.data.model.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompleteViewModel @Inject constructor(
    private val registerRepository: RegisterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompleteUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onGenderChange(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onAvatarChange(avatar: String) {
        _uiState.update { it.copy(avatar = avatar) }
    }

    fun complete() {
        try {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                registerRepository.completeProfile(
                    name = _uiState.value.name,
                    gender = _uiState.value.gender,
                    avatar = _uiState.value.avatar
                )
                _uiState.update { it.copy(completeSuccess = true, isLoading = false) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message ?: "注册失败", isLoading = false) }
        }
    }
}