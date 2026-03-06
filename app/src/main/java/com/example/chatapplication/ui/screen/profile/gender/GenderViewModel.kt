package com.example.chatapplication.ui.screen.profile.gender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplication.data.repository.ProfileRepository
import com.example.chatapplication.data.repository.SessionRepository
import com.example.chatapplication.data.model.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenderViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GenderUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCurrentGender()
    }

    private fun loadCurrentGender() {
        viewModelScope.launch {
            val userId = sessionRepository.observeUserId().first()!!
            profileRepository.observeProfile(userId = userId).collect { contact ->
                _uiState.update {
                    it.copy(gender = contact.gender)
                }
            }
        }
    }

    fun onGenderChange(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun updateGender() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = sessionRepository.observeUserId().first()!!
            val response = profileRepository.updateProfile(
                id = userId, gender = _uiState.value.gender
            )
            if (response == true) {
                _uiState.update {
                    it.copy(isSuccess = true, isLoading = false)
                }
            } else {
                _uiState.update {
                    it.copy(error = "更新性别失败", isLoading = false)
                }
            }
        }
    }
}