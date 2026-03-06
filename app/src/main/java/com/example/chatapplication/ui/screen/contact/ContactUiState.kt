package com.example.chatapplication.ui.screen.contact

import com.example.chatapplication.data.model.Contact

data class ContactUiState(
    val contacts: List<Contact> = emptyList(),
    val searchQuery: String = "",
    val hasSearched: Boolean = false,
    val searchResults: List<Contact>? = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)