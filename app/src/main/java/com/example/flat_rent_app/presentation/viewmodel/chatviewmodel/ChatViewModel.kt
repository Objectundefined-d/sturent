package com.example.flat_rent_app.presentation.viewmodel.chatviewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.model.Message
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.BlackListRepository
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

    private val _state = MutableStateFlow(ChatUiState(myUid = myUid, chatId = chatId, otherUid = otherUid))
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    val otherProfile: StateFlow<UserProfile?> =
        profileRepo.observerProfile(otherUid)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val messages: StateFlow<List<Message>> =
        chatRepo.observeMessages(chatId, limit = 200)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onInput(v: String) {
        _state.update { it.copy(input = v, error = null) }
    }

    fun send() = viewModelScope.launch {
        val inputText = _state.value.input
        if (inputText.isBlank()) return@launch

        _state.update { it.copy(input = "") }

        chatRepo.sendMessage(_state.value.chatId, _state.value.otherUid, inputText)
            .onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Ошибка") }
            }
    }

    fun markRead() = viewModelScope.launch {
        chatRepo.markRead(chatId)
    }

    fun deleteMessage(messageId: String, forBoth: Boolean) = viewModelScope.launch {
        chatRepo.deleteMessage(state.value.chatId, messageId, forBoth)
            .onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Ошибка удаления") }
            }
    }

    fun clearHistory(forBoth: Boolean) = viewModelScope.launch {
        chatRepo.clearHistory(state.value.chatId, forBoth)
            .onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Ошибка удаления") }
            }
    }

    fun editMessage(messageId: String, newText: String) = viewModelScope.launch {
        chatRepo.editMessage(state.value.chatId, messageId, newText)
            .onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Ошибка редактирования") }
            }
    }

    fun openProfile() {
        _state.update { it.copy(showProfileDetails = true) }
    }

    fun closeProfileDetails() {
        _state.update {
            it.copy(showProfileDetails = false)
        }
    }

    fun setSearchActive(active: Boolean) {
        _state.update {
            it.copy(isSearchActive = active, searchQuery = if (!active) "" else it.searchQuery)
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun clearSearch() {
        _state.update { it.copy(isSearchActive = false, searchQuery = "") }
    }
}