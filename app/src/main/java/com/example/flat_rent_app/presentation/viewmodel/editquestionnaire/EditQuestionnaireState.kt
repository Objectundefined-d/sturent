package com.example.flat_rent_app.presentation.viewmodel.editquestionnaire
import com.example.flat_rent_app.domain.model.ProfilePhoto
import com.example.flat_rent_app.util.Constants

data class EditQuestionnaireState(
    val name: String = "",
    val city: String = "",
    val eduPlace: String = "",
    val description: String = "",

    val selectedHabits: Map<String, Boolean> = mapOf(
        Constants.SMOKING_ALLOWED to false,
        Constants.DRINKS_ALCOHOL to false,
        Constants.NIGHT_OWL to false,
        Constants.EARLY_BIRD to false,
        Constants.HAS_PETS to false,
        Constants.INVITES_GUESTS to false,
        Constants.VALUES_CLEANLINESS to false,
        Constants.VALUES_QUIET to false,
        Constants.LOVES_MUSIC to false,
        Constants.DOES_SPORTS to false,
    ),

    val createdAtMillis: Long? = null,
    val photoSlots: List<ProfilePhoto?> = listOf(null, null, null),
    val mainPhotoIndex: Int = 0,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)