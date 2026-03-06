package com.example.chatapplication.ui.screen.profile.gender

import com.example.chatapplication.data.model.Gender

data class GenderUiState (
    val gender: Gender = Gender.UNKNOWN,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)