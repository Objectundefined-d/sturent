package com.example.flat_rent_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.model.AuthUser
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.ProfileRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    authRepo: AuthRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

    val user: StateFlow<AuthUser?> =
        authRepo.currentUser
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val profile: StateFlow<UserProfile?> =
        user.flatMapLatest { u ->
            if (u == null) flowOf(null)
            else profileRepo.observerProfile(u.uid)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val isProfileComplete: StateFlow<Boolean> =
        profile.map { p -> p?.isComplete() == true }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            user.collect { u ->
                if (u != null) {
                    saveFcmToken()
                }
            }
        }
    }

    private fun saveFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            viewModelScope.launch {
                try {
                    profileRepo.saveFcmToken(token)
                } catch (ignored: Exception) {
                }
            }
        }
    }
}
