package com.example.flat_rent_app.presentation.viewmodel.authviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun onEmail(v: String) = _state.update { it.copy(email = v, error = null) }
    fun onPassword(v: String) = _state.update { it.copy(password = v, error = null) }
    fun onConfirm(v: String) = _state.update { it.copy(confirm = v, error = null) }

    fun login() = viewModelScope.launch {
        val s = _state.value
        val err = validateEmailPass(s.email, s.password)
        if (err != null) {
            _state.update { it.copy(error = err) }
            return@launch
        }

        _state.update { it.copy(loading = true, error = null) }
        authRepo.signIn(s.email, s.password)
            .onSuccess { _state.update { it.copy(loading = false) } }
            .onFailure { e -> _state.update { it.copy(loading = false, error = e.message ?: "Ошибка входа") } }
    }

    fun register() = viewModelScope.launch {
        val s = _state.value
        val err = validateRegister(s.email, s.password, s.confirm)
        if (err != null) {
            _state.update { it.copy(error = err) }
            return@launch
        }

        _state.update { it.copy(loading = true, error = null) }
        authRepo.signUp(s.email, s.password)
            .onSuccess { _state.update { it.copy(loading = false) } }
            .onFailure { e -> _state.update { it.copy(loading = false, error = e.message ?: "Ошибка регистрации") } }
    }

    private fun validateEmailPass(email: String, pass: String): String? {
        if (email.isBlank()) return "Введите email"
        if (!email.contains("@")) return "Некорректный email"
        if (pass.length < 6) return "Пароль минимум 6 символов"
        return null
    }

    private fun validateRegister(email: String, pass: String, confirm: String): String? {
        val base = validateEmailPass(email, pass)
        if (base != null) return base
        if (pass != confirm) return "Пароли не совпадают"
        return null
    }
}
