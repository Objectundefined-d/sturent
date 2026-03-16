package com.example.flat_rent_app.presentation.viewmodel.favoritesviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.domain.repository.ProfileRepository
import com.example.flat_rent_app.domain.repository.SwipeRepository
import com.example.flat_rent_app.util.LikeOutCome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val swipeRepository: SwipeRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state

    init {
        loadFavorites()
    }

    fun retry() {
        loadFavorites()
    }

    fun loadFavorites() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                swipeRepository.observeFavorites().collect { userIds ->
                    if (userIds.isEmpty()) {
                        _state.update { it.copy(profiles = emptyList(), isLoading = false) }
                        return@collect
                    }

                    val profiles = userIds.mapNotNull { uid ->
                        profileRepository.observerProfile(uid).firstOrNull()
                    }

                    _state.update { it.copy(profiles = profiles, isLoading = false) }
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
    }

    fun openProfile(profile: UserProfile) {
        _state.update { it.copy(selectedProfile = profile) }
    }

    fun closeProfile() {
        _state.update { it.copy(selectedProfile = null) }
    }

    fun swipeRight(targetId: String) {
        viewModelScope.launch {
            swipeRepository.removeFromFavorites(targetId)
            swipeRepository.likeUser(targetId)
                .onSuccess { outcome ->
                    when (outcome) {
                        is LikeOutCome.Match -> {
                            _state.update {
                                it.copy(
                                    matchChatId = outcome.chatId,
                                    matchedUserId = targetId
                                )
                            }
                        }
                        LikeOutCome.LikedOnly -> { }
                    }
                }
        }
    }

    fun swipeLeft(targetId: String) {
        viewModelScope.launch {
            swipeRepository.removeFromFavorites(targetId)
        }
    }

    fun dismissMatch() {
        val matchId = _state.value.matchChatId
        if (matchId != null) {

            viewModelScope.launch {
                swipeRepository.markMatchAsSeen(matchId)
            }
        }

        _state.update { it.copy(matchChatId = null, matchedUserId = null) }
    }
}