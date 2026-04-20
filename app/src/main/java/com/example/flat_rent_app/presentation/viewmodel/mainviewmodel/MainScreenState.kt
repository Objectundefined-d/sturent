package com.example.flat_rent_app.presentation.viewmodel.mainviewmodel

import com.example.flat_rent_app.domain.model.SwipeProfile


data class MainScreenState(
    val profiles: List<SwipeProfile> = emptyList(),
    val currentIndex: Int = -1,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAllViewed: Boolean = false,
    val showProfileDetails: Boolean = false,
    val selectedProfile: SwipeProfile? = null,
    val selectedUniversityFilter: String = Constants.UNIVERSITY_ALL,
    val selectedGenderFilter: String = Constants.GENDER_ANY,
    val ageFilterMin: Int = Constants.AGE_MIN_DEFAULT,
    val ageFilterMax: Int = Constants.AGE_MAX_DEFAULT,
    val showFilters: Boolean = false,
    val matchChatId: String? = null,
    val matchedUserId: String? = null
)
