package com.example.flat_rent_app.presentation.viewmodel.blacklistviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.domain.repository.BlackListRepository
import com.example.flat_rent_app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlackListViewModel @Inject constructor(
    private val blackListRepo: BlackListRepository,
    private val profileRepo: ProfileRepository,
    private val event: BlackListEvent
) : ViewModel() {
    private val _state = MutableStateFlow(BlackListUiState())
    val state: StateFlow<BlackListUiState> = _state

    init {
        loadBlockedPeople()
    }

    fun retry() {
        loadBlockedPeople()
    }

    fun loadBlockedPeople() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                blackListRepo.observeBlockedUsers().collect { userIds ->
                    if (userIds.isEmpty()) {
                        _state.update { it.copy(profiles = emptyList(), isLoading = false) }
                        return@collect
                    }

                    val profiles = userIds.mapNotNull { uid ->
                        profileRepo.observerProfile(uid).firstOrNull()
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

    fun blockUser(userId: String) {
        viewModelScope.launch {
            blackListRepo.blockUser(userId)
            _state.update { it.copy(profileBlocked = true) }
            event.notifyChanged()
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            blackListRepo.unblockUser(userId)
            _state.update { it.copy(profileBlocked = false) }
            event.notifyChanged()
        }
    }

    fun checkIsBlocked(userId: String) {
        viewModelScope.launch {
            val blocked = blackListRepo.isUserBlocked(userId)
            _state.update { it.copy(profileBlocked = blocked) }
        }
    }
}