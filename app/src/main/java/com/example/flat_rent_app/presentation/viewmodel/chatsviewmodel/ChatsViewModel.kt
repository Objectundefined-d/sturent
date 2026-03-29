package com.example.flat_rent_app.presentation.viewmodel.chatsviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.model.ChatUiItem
import com.example.flat_rent_app.domain.repository.ChatRepository
import com.example.flat_rent_app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    chatRepo: ChatRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQuery(query: String) {
        _searchQuery.value = query
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private val allItems: StateFlow<List<ChatUiItem>> =
        chatRepo.observeMyChats()
            .flatMapLatest { chats ->
                if (chats.isEmpty()) return@flatMapLatest flowOf(emptyList())
                combine(
                    chats.map { chat ->
                        profileRepo.observerProfile(chat.otherUid)
                            .map { p -> ChatUiItem(chat, p) }
                    }
                ) { arr -> arr.toList() }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val items: StateFlow<List<ChatUiItem>> =
        _searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                allItems.map { chats ->
                    if (query.isEmpty()) chats
                    else chats.filter { item ->
                        val name = item.profile?.name ?: item.chat.otherUid
                        name.contains(query, ignoreCase = true)
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}