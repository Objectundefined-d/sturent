package com.example.flat_rent_app.presentation.viewmodel.chatviewmodel

data class ChatUiState(
    val myUid: String = "",
    val chatId: String = "",
    val otherUid: String = "",
    val input: String = "",
    val sending: Boolean = false,
    val error: String? = null,
    val showProfileDetails: Boolean = false,
    val isSearchActive: Boolean = false,
    val searchQuery: String = ""
)