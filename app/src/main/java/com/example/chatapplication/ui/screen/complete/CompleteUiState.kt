package com.example.chatapplication.ui.screen.complete

import com.example.chatapplication.data.model.Gender

data class CompleteUiState (
    val name: String = "",
    val gender: Gender = Gender.UNKNOWN,
    val avatar: String = "",
    val isLoading: Boolean = false,
    val completeSuccess: Boolean = false,
    val error: String? = null
)