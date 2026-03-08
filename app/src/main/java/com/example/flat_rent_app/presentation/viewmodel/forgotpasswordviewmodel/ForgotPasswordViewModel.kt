package com.example.flat_rent_app.presentation.viewmodel.forgotpasswordviewmodel

import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.getValue
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val auth : FirebaseAuth
) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isSuccess by mutableStateOf(false)
        private set

    fun onEmailChange(value: String) {
        email = value
        errorMessage = null
    }

    fun sendResetEmail() {
        if (email.isBlank()) {
            errorMessage = "Введите email"
        }

        isLoading = true
        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    isSuccess = true
                } else {
                    errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException ->
                            "Пользователь с таким email не найден"
                        is FirebaseAuthInvalidCredentialsException ->
                            "Неверный формат email"
                        else -> "Ошибка: ${task.exception?.message}"
                    }
                }
            }
    }

    fun resetSuccess() {
        isSuccess = false
    }
}