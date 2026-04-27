package com.example.flat_rent_app.presentation.viewmodel.authviewmodel

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val loading: Boolean = false,
    val error: String? = null
)
