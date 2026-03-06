package com.example.chatapplication.ui.screen.contactProfile

import com.example.chatapplication.data.model.Contact

data class ContactProfileUiState (
    val contact: Contact? = null,
    val isMyProfile: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)