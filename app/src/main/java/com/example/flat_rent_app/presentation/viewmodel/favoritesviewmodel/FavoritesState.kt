package com.example.flat_rent_app.presentation.viewmodel.favoritesviewmodel

import com.example.flat_rent_app.domain.model.UserProfile

data class FavoritesState(
    val profiles: List<UserProfile> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedProfile: UserProfile? = null,
    val matchChatId: String? = null,
    val matchedUserId: String? = null
)