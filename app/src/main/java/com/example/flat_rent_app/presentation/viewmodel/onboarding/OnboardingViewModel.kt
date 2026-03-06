package com.example.flat_rent_app.presentation.viewmodel.onboarding

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.model.OnboardingState
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.PhotoRepository
import com.example.flat_rent_app.domain.repository.ProfileRepository
import com.example.flat_rent_app.presentation.util.UriFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val profileRepo: ProfileRepository,
    private val photoRepo: PhotoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun onName(v: String) = _state.update { it.copy(name = v, error = null) }
    fun onCity(v: String) = _state.update { it.copy(city = v, error = null) }
    fun onEduPlace(v: String) = _state.update { it.copy(eduPlace = v, error = null) }
    fun onDescription(v: String) = _state.update { it.copy(description = v, error = null) }


    fun togglePreference(pref: String) = _state.update { s ->
        val next = if (pref in s.preferences) s.preferences - pref else s.preferences + pref
        s.copy(preferences = next, error = null)
    }

    fun onPickedPhoto(uri: Uri?) {
        _state.value = _state.value.copy(pickedPhotoUri = uri, error = null)
    }

    fun uploadMainPhoto(context: Context) {
        val uri = _state.value.pickedPhotoUri ?: run {
            _state.value = _state.value.copy(error = "Выберите фото")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            val file = runCatching { UriFiles.copyToCache(context, uri) }
                .getOrElse { e ->
                    _state.value = _state.value.copy(loading = false, error = e.message ?: "Не удалось прочитать файл")
                    return@launch
                }

            val res = photoRepo.uploadPhoto(0, file)
            res.fold(
                onSuccess = { photo ->
                    _state.value = _state.value.copy(loading = false, uploadedPhoto = photo)
                },
                onFailure = { t ->
                    _state.value = _state.value.copy(loading = false, error = t.message ?: "Ошибка загрузки")
                }
            )
        }
    }

    fun saveProfile() {
        val uid = authRepo.currentUid() ?: run {
            _state.value = _state.value.copy(error = "Нет авторизации")
            return
        }

        val s = _state.value
        if (s.name.isBlank() || s.city.isBlank() || s.eduPlace.isBlank()) {
            _state.value = s.copy(error = "Заполните имя, город и вуз")
            return
        }
        if (s.uploadedPhoto == null) {
            _state.value = s.copy(error = "Загрузите фото")
            return
        }
        if (s.description.isBlank()) {
            _state.value = s.copy(error = "Добавьте описание")
            return
        }

        viewModelScope.launch {
            _state.value = s.copy(loading = true, saved = false, error = null)

            val profile = UserProfile(
                uid = uid,
                name = s.name.trim(),
                city = s.city.trim(),
                eduPlace = s.eduPlace.trim(),
                description = s.description.trim(),
                mainPhotoIndex = 0,
                photoSlots = listOf(s.uploadedPhoto, null, null),
                preferences = s.preferences.toList()
            )

            val res = profileRepo.upsertMyProfile(profile)
            res.fold(
                onSuccess = {
                    _state.value = _state.value.copy(loading = false, saved = true)
                },
                onFailure = { t ->
                    _state.value = _state.value.copy(loading = false, error = t.message ?: "Ошибка сохранения")
                }
            )
        }
    }
}
