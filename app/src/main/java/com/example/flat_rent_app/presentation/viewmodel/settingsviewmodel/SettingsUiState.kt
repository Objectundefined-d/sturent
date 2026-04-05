package com.example.flat_rent_app.presentation.viewmodel.settingsviewmodel

data class SettingsUiState(
    val notifyMatches: Boolean = true,
    val notifyMessages: Boolean = true,
    val isDarkTheme: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)