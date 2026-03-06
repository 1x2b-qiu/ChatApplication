package com.example.chatapplication.ui.screen.profile.main // 个人资料页面模型包名

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

@HiltViewModel // 标记为 Hilt 管理的 ViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() { // 继承 ViewModel

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeProfile()
    }

    fun observeProfile() {
        try {
            viewModelScope.launch {
                val userId = sessionRepository.observeUserId().first()!!
                profileRepository.observeProfile(userId = userId).collect { contact ->
                    _uiState.update {
                        it.copy(
                            contact = contact
                        )
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    error = e.message
                )
            }
        }
    }

    //更新头像
    fun updateAvatar(avatar: String) {
        viewModelScope.launch {
            _uiState.value.contact?.id?.let { userId ->
                _uiState.update { it.copy(contact = it.contact?.copy(avatar = avatar)) }
                profileRepository.updateProfile(
                    id = userId, avatar = avatar
                )
            }
        }
    }


}
