package com.example.flat_rent_app.domain.model

import android.net.Uri

data class OnboardingState(
    val name: String = "",
    val age: String = "",
    val city: String = "",
    val eduPlace: String = "",
    val description: String = "",
    val pickedPhotoUris: List<Uri?> = listOf(null, null, null),
    val uploadedPhotos: List<ProfilePhoto?> = listOf(null, null, null),
    val mainPhotoIndex: Int = 0,
    val loading: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
    val preferences: Set<String> = emptySet(),
)