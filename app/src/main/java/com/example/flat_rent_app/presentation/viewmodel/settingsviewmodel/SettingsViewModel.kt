package com.example.flat_rent_app.presentation.viewmodel.settingsviewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import androidx.core.content.edit

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepo: AuthRepository,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(
        SettingsUiState(
            isDarkTheme = prefs.getBoolean("dark_theme", false)
        )
    )

    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        val uid = authRepo.currentUid() ?: return@launch

        try {
            val doc = db.collection("users").document(uid).get().await()
            val matchesVal = doc.getBoolean("notifyMatches") ?: true
            val messagesVal = doc.getBoolean("notifyMessages") ?: true

            prefs.edit {
                putBoolean("notifyMatches", matchesVal).putBoolean("notifyMessages", messagesVal)
            }

            _state.update {
                it.copy(
                    notifyMatches = matchesVal,
                    notifyMessages = messagesVal,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Ошибка загрузки: ${e.message}"
                )
            }
        }
    }

    fun setNotifyMatches(newValue: Boolean) {
        val uid = authRepo.currentUid() ?: return

        _state.update {
            it.copy(notifyMatches = newValue)
        }
        prefs.edit {
            putBoolean("notifyMatches", newValue)
        }

        db.collection("users").document(uid).update("notifyMatches", newValue)
    }

    fun setNotifyMessages(newValue: Boolean) {
        val uid = authRepo.currentUid() ?: return

        _state.update {
            it.copy(notifyMessages = newValue)
        }
        prefs.edit {
            putBoolean("notifyMessages", newValue)
        }

        db.collection("users").document(uid).update("notifyMessages", newValue)
    }

    fun setNewTheme(newValue: Boolean) {
        _state.update { it.copy(isDarkTheme = newValue) }
        prefs.edit {
            putBoolean("dark_theme", newValue)
        }
    }

    fun sendPasswordReset() = viewModelScope.launch {
        val email = authRepo.currentUserEmail() ?: run {
            _state.update { it.copy(error = "Не авторизован") }
            return@launch
        }
        authRepo.sendPasswordReset(email)
            .onSuccess { _state.update { it.copy(passwordResetSent = true) } }
            .onFailure { e -> _state.update { it.copy(error = e.message ?: "Ошибка отправки") } }
    }

    fun consumePasswordReset() {
        _state.update { it.copy(passwordResetSent = false) }
    }

    fun sendEmailVerification() = viewModelScope.launch {
        authRepo.sendEmailVerification()
            .onSuccess { _state.update { it.copy(emailVerificationSent = true) } }
            .onFailure { e -> _state.update { it.copy(actionError = e.message ?: "Ошибка") } }
    }

    fun consumeActionError() {
        _state.update { it.copy(actionError = null) }
    }

    fun updateEmail(newEmail: String, password: String) = viewModelScope.launch {
        authRepo.updateEmail(newEmail, password)
            .onSuccess { _state.update { it.copy(emailUpdateSent = true) } }
            .onFailure { e -> _state.update { it.copy(error = e.message ?: "Ошибка") } }
    }

    fun consumeEmailVerification() {
        _state.update { it.copy(emailVerificationSent = false) }
    }

    fun consumeEmailUpdate() {
        _state.update { it.copy(emailUpdateSent = false) }
    }

    fun openBlackList() {
        _state.update { it.copy(showBlackList = true) }
    }
}