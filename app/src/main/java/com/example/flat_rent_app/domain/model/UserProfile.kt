package com.example.flat_rent_app.domain.model

enum class Gender {
    MALE,
    FEMALE
}


    data class UserProfile(
        val uid: String = "",
        val name: String = "",
        val gender: Gender? = null,
        val age: Int? = null,
        val city: String = "",
        val eduPlace: String = "",
        val description: String = "",
        val mainPhotoIndex: Int = 0,
        val photoSlots: List<ProfilePhoto?> = listOf(null, null, null),
        val preferences: List<String> = emptyList(),
        val createdAtMillis: Long? = null,
        val updatedAtMillis: Long? = null,
) {
    fun isComplete(): Boolean =
        name.isNotBlank() && age != null && city.isNotBlank() && eduPlace.isNotBlank() && description.isNotBlank()
}
