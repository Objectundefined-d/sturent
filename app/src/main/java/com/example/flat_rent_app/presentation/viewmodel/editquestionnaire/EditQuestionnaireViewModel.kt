package com.example.flat_rent_app.presentation.viewmodel.editquestionnaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.ProfileRepository
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
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditQuestionnaireState())
    val state: StateFlow<EditQuestionnaireState> = _state

    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name) }
    }

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
                    city = state.value.city,
                    eduPlace = state.value.eduPlace,
                    description = state.value.description,
                    mainPhotoIndex = 0,
                    preferences = selectedHabitsList,
                    photoSlots = state.value.photoSlots,
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
                            city = p.city,
                            eduPlace = p.eduPlace,
                            description = p.description.substringBefore("\n\nПривычки:"),
                            selectedHabits = mergeHabits(
                                currentHabits = it.selectedHabits,
                                loadedHabits = p.preferences
                            ),
                            photoSlots = p.photoSlots,
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
}