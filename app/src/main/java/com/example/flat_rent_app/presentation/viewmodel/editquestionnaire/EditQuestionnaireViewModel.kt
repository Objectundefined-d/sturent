package com.example.flat_rent_app.presentation.viewmodel.editquestionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.model.Gender
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.PhotoRepository
import com.example.flat_rent_app.domain.repository.ProfileRepository
import com.example.flat_rent_app.presentation.util.UriFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditQuestionnaireViewModel @Inject constructor(
    private val profileRepo: ProfileRepository,
    private val authRepo: AuthRepository,
    private val photoRepo: PhotoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditQuestionnaireState())
    val state: StateFlow<EditQuestionnaireState> = _state

    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun onAgeChanged(age: String) = _state.update { it.copy(age = age) }

    fun onGenderChanged(g: Gender) = _state.update { it.copy(gender = g) }

    fun onCityChanged(city: String) {
        _state.update { it.copy(city = city) }
    }

    fun onEduPlaceChanged(eduPlace: String) {
        _state.update { it.copy(eduPlace = eduPlace) }
    }

    fun onDescriptionChanged(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun toggleHabit(habitKey: String) {
        _state.update { currentState ->
            val currentValue = currentState.selectedHabits[habitKey] ?: false
            val updatedHabits = currentState.selectedHabits.toMutableMap()
            updatedHabits[habitKey] = !currentValue
            currentState.copy(selectedHabits = updatedHabits)
        }
    }

    val selectedHabitsList: List<String>
        get() = state.value.selectedHabits
            .filter { it.value }
            .map { it.key }

    fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = authRepo.currentUid() ?: throw Exception("Не авторизован")

                val userProfile = UserProfile(
                    uid = userId,
                    name = state.value.name,
                    age = state.value.age.toIntOrNull(),
                    gender = state.value.gender,
                    city = state.value.city,
                    eduPlace = state.value.eduPlace,
                    description = state.value.description,
                    preferences = selectedHabitsList,
                    photoSlots = state.value.photoSlots,
                    mainPhotoIndex = state.value.mainPhotoIndex,
                    createdAtMillis = state.value.createdAtMillis,
                    updatedAtMillis = System.currentTimeMillis()
                )

                val result = profileRepo.upsertMyProfile(userProfile)

                result.onSuccess {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка"
                    )
                }
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val userId = authRepo.currentUid() ?: return@launch
                val profile = profileRepo.observerProfile(userId).firstOrNull()

                profile?.let { p ->
                    _state.update {
                        it.copy(
                            name = p.name,
                            age = p.age?.toString() ?: "",
                            gender = p.gender,
                            city = p.city,
                            eduPlace = p.eduPlace,
                            description = p.description.substringBefore("\n\nПривычки:"),
                            selectedHabits = mergeHabits(
                                currentHabits = it.selectedHabits,
                                loadedHabits = p.preferences
                            ),
                            photoSlots = p.photoSlots,
                            mainPhotoIndex = p.mainPhotoIndex,
                            createdAtMillis = p.createdAtMillis,
                            isLoading = false
                        )
                    }
                } ?: run {
                    val currentUser = authRepo.currentUser.firstOrNull()
                    val emailName = currentUser?.email?.substringBefore("@") ?: ""
                    _state.update {
                        it.copy(
                            name = emailName,
                            isLoading = false
                        )
                    }
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось загрузить профиль"
                    )
                }
            }
        }
    }

    private fun mergeHabits(
        currentHabits: Map<String, Boolean>,
        loadedHabits: List<String>
    ): Map<String, Boolean> {
        val result = currentHabits.toMutableMap()
        loadedHabits.forEach { habit ->
            if (result.containsKey(habit)) {
                result[habit] = true
            }
        }
        return result
    }

    init {
        loadProfile()
    }

    fun uploadPhoto(context: android.content.Context, index: Int, uri: android.net.Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val file = runCatching { UriFiles.copyToCache(context, uri) }
                .getOrElse { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                    return@launch
                }
            photoRepo.uploadPhoto(index, file).fold(
                onSuccess = { photo ->
                    val updated = _state.value.photoSlots.toMutableList().also { it[index] = photo }
                    _state.update { it.copy(isLoading = false, photoSlots = updated) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    fun deletePhoto(index: Int) {
        val slots = _state.value.photoSlots.toMutableList().also { it[index] = null }
        val currentMain = _state.value.mainPhotoIndex

        val newMain = if (currentMain == index) {
            slots.indexOfFirst { it?.fullUrl != null }.coerceAtLeast(0)
        } else {
            currentMain
        }

        _state.update { it.copy(photoSlots = slots, mainPhotoIndex = newMain) }
    }

    fun setMainPhoto(index: Int) {
        _state.update { it.copy(mainPhotoIndex = index) }
    }
}