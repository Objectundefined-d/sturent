package com.example.flat_rent_app.presentation.viewmodel.mainviewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.model.SwipeProfile
import com.example.flat_rent_app.domain.repository.ProfileRepository
import com.example.flat_rent_app.domain.repository.SwipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val swipeRepository: SwipeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state

    init {
        loadProfiles()
    }

    fun loadProfiles() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                delay(2000L)

                val result = profileRepository.getFeedProfiles(limit = 10)

                result.onSuccess { userProfiles ->
                    val swipeProfiles = userProfiles.map { userProfile ->
                        SwipeProfile(
                            uid = userProfile.uid,
                            name = extractName(userProfile.name),
                            age = extractAgeFromDescription(userProfile.description),
                            city = userProfile.city,
                            university = userProfile.eduPlace,
                            description = userProfile.description,
                            lookingFor = extractLookingFor(userProfile.description),
                            photoUrl = userProfile.photoSlots
                                .getOrNull(userProfile.mainPhotoIndex)
                                ?.fullUrl
                        )
                    }

                    _state.update {
                        it.copy(
                            profiles = swipeProfiles,
                            isLoading = false,
                            currentIndex = if (swipeProfiles.isNotEmpty()) 0 else -1
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Ошибка загрузки: ${error.message}"
                        )
                    }
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка: ${e.message}"
                    )
                }
            }
        }
    }

    fun swipeRight() {
        val currentIndex = _state.value.currentIndex
        val profiles = _state.value.profiles

        if (currentIndex in profiles.indices) {
            val targetId = profiles[currentIndex].uid

            viewModelScope.launch {
                swipeRepository.likeUser(targetId)
                    .onSuccess { outcome ->
                        println("Лайк отправлен: $outcome")
                    }
                    .onFailure { error ->
                        println("Ошибка лайка: ${error.message}")
                    }

                moveToNext()
            }
        }
    }

    fun swipeLeft() {
        val currentIndex = _state.value.currentIndex
        val profiles = _state.value.profiles

        if (currentIndex in profiles.indices) {
            val targetId = profiles[currentIndex].uid

            viewModelScope.launch {
                swipeRepository.passUser(targetId)
                    .onSuccess {
                        println("Пас отправлен")
                    }
                    .onFailure { error ->
                        println("Ошибка паса: ${error.message}")
                    }

                moveToNext()
            }
        }
    }

    private fun moveToNext() {
        val nextIndex = _state.value.currentIndex + 1
        val profiles = _state.value.profiles

        _state.update { state ->
            if (nextIndex < profiles.size) {
                state.copy(currentIndex = nextIndex)
            } else {
                state.copy(
                    currentIndex = if (profiles.isNotEmpty()) 0 else -1,
                    showAllViewed = profiles.isNotEmpty()
                )
            }
        }
    }

    fun retry() {
        loadProfiles()
    }

    private fun extractName(fullName: String): String {
        return fullName.split(",").firstOrNull()?.trim() ?: fullName
    }

    private fun extractAgeFromDescription(description: String): Int {
        return (18..35).random()
    }

    private fun extractLookingFor(description: String): String {
        return when {
            description.contains("работ", ignoreCase = true) -> "Работаю"
            description.contains("учусь", ignoreCase = true) -> "Учусь"
            description.contains("студент", ignoreCase = true) -> "Студент"
            else -> "Ищу соседа"
        }
    }


    fun openProfileDetails() {
        val currentIndex = _state.value.currentIndex
        val profiles = _state.value.profiles

        if (currentIndex in profiles.indices) {
            _state.update {
                it.copy(
                    showProfileDetails = true,
                    selectedProfile = profiles[currentIndex]
                )
            }
        }
    }

    fun closeProfileDetails() {
        _state.update {
            it.copy(
                showProfileDetails = false,
                selectedProfile = null
            )
        }
    }
}