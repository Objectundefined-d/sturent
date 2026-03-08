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
    private val photoRepo: PhotoRepository
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

    fun uploadPhoto(context: android.content.Context, index: Int, uri: Uri) {
        viewModelScope.launch {
            val file = runCatching { UriFiles.copyToCache(context, uri) }
                .getOrElse { return@launch }

            photoRepo.uploadPhoto(index, file).fold(
                onSuccess = { photo ->
                    val current = userProfile.value ?: return@fold
                    val slots = current.photoSlots.toMutableList().also { it[index] = photo }
                    profileRepo.upsertMyProfile(current.copy(photoSlots = slots))
                },
                onFailure = { /* хз пустой */  }
            )
        }
    }

    fun deletePhoto(index: Int) {
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            val slots = current.photoSlots.toMutableList().also { it[index] = null }
            val newMain = if (current.mainPhotoIndex == index)
                slots.indexOfFirst { it != null }.coerceAtLeast(0)
            else
                current.mainPhotoIndex

            profileRepo.upsertMyProfile(current.copy(photoSlots = slots, mainPhotoIndex = newMain))
        }
    }

    fun setMainPhoto(index: Int) {
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            profileRepo.upsertMyProfile(current.copy(mainPhotoIndex = index))
        }
    }
}