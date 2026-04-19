package com.example.flat_rent_app.presentation.viewmodel.settingsviewmodel

data class SettingsUiState(
    val notifyMatches: Boolean = true,
    val notifyMessages: Boolean = true,
    val isDarkTheme: Boolean = false,
    val isLoading: Boolean = false,
    val showBlackList: Boolean = false,
    val error: String? = null,
    val actionError: String? = null,
    val passwordResetSent: Boolean = false,
    val emailVerificationSent: Boolean = false,
    val emailUpdateSent: Boolean = false
)