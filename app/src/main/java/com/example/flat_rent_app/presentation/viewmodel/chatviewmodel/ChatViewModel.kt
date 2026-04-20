package com.example.flat_rent_app.presentation.viewmodel.chatviewmodel

import android.R
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.model.Message
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.ChatRepository
import com.example.flat_rent_app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepo: ChatRepository,
    authRepo: AuthRepository,
    profileRepo: ProfileRepository
) : ViewModel() {

    private val chatId: String = savedStateHandle["chatId"] ?: ""
    private val otherUid: String = savedStateHandle["otherUid"] ?: ""
    private val myUid: String = authRepo.currentUid().orEmpty()

    private val _ui = MutableStateFlow(ChatUiState(myUid = myUid, chatId = chatId, otherUid = otherUid))
    val ui: StateFlow<ChatUiState> = _ui.asStateFlow()

    val otherProfile: StateFlow<UserProfile?> =
        profileRepo.observerProfile(otherUid)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val messages: StateFlow<List<Message>> =
        chatRepo.observeMessages(chatId, limit = 200)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onInput(v: String) {
        _ui.update { it.copy(input = v, error = null) }
    }

    fun send() = viewModelScope.launch {
        val s = _ui.value
        if (s.input.isBlank()) return@launch

        _ui.update { it.copy(sending = true, error = null) }
        chatRepo.sendMessage(s.chatId, s.otherUid, s.input)
            .onSuccess { _ui.update { it.copy(input = "", sending = false) } }
            .onFailure { e -> _ui.update { it.copy(sending = false, error = e.message ?: "Ошибка") } }
    }

    fun markRead() = viewModelScope.launch {
        chatRepo.markRead(chatId)
    }

    fun deleteMessage(messageId: String, forBoth: Boolean) = viewModelScope.launch {
        chatRepo.deleteMessage(ui.value.chatId, messageId, forBoth)
            .onFailure { e ->
                _ui.update { it.copy(error = e.message ?: "Ошибка удаления") }
            }
    }

    fun clearHistory(forBoth: Boolean) = viewModelScope.launch {
        chatRepo.clearHistory(ui.value.chatId, forBoth)
            .onFailure { e ->
                _ui.update { it.copy(error = e.message ?: "Ошибка удаления") }
            }
    }
}
