package com.example.flat_rent_app.domain.model

data class SwipeProfile(
    val uid: String = "",
    val name: String = "",
    val age: Int? = 0,
    val gender: Gender? = null,
    val city: String = "",
    val university: String = "",
    val description: String = "",
    val lookingFor: String = "",
    val photoUrl: String? = null
)