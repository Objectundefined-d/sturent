package com.example.flat_rent_app.presentation.viewmodel.blacklistviewmodel

import com.example.flat_rent_app.domain.model.UserProfile

data class BlackListUiState(
    val profiles: List<UserProfile> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedProfile: UserProfile? = null,
    val profileBlocked: Boolean = false
)