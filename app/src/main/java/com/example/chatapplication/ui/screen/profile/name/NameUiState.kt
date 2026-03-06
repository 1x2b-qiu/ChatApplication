package com.example.chatapplication.ui.screen.profile.name

data class NameUiState (
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
