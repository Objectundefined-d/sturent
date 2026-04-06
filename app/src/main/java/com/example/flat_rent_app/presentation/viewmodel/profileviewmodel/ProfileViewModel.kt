package com.example.flat_rent_app.presentation.viewmodel.profileviewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.PhotoRepository
import com.example.flat_rent_app.domain.repository.ProfileRepository
import com.example.flat_rent_app.presentation.util.UriFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.fold

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val profileRepo: ProfileRepository,
) : ViewModel() {

    val user = authRepo.currentUser

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfile = user.flatMapLatest { currentUser ->
        if (currentUser != null) {
            profileRepo.observerProfile(currentUser.uid)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun signOut() {
        viewModelScope.launch { authRepo.signOut() }
    }

    private val _deleteState = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteState

    fun deleteAccount() {
        viewModelScope.launch {
            authRepo.deleteAccount()
                .onFailure { _deleteState.value = it.message }
        }
    }
}