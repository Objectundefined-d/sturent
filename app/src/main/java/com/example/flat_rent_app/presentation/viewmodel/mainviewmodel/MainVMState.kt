package com.example.flat_rent_app.presentation.viewmodel.mainviewmodel

import com.example.flat_rent_app.domain.model.SwipeProfile
import com.example.flat_rent_app.util.Constants


data class MainScreenState(
    val profiles: List<SwipeProfile> = emptyList(),
    val currentIndex: Int = -1,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAllViewed: Boolean = false,
    val showProfileDetails: Boolean = false,
    val selectedProfile: SwipeProfile? = null,
    val selectedUniversityFilter: String = Constants.UNIVERSITY_ALL,
    val matchChatId: String? = null
)