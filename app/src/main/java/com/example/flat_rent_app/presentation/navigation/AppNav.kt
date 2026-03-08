package com.example.flat_rent_app.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.flat_rent_app.presentation.navigation.appgraphs.AppGraph
import com.example.flat_rent_app.presentation.navigation.appgraphs.AuthGraph
import com.example.flat_rent_app.presentation.navigation.appgraphs.OnboardingGraph
import com.example.flat_rent_app.presentation.viewmodel.RootViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun AppNav() {
    val rootVm: RootViewModel = hiltViewModel()
    val user by rootVm.user.collectAsState()
    val isComplete by rootVm.isProfileComplete.collectAsState()

    when {
        user == null -> AuthGraph()
        !isComplete -> OnboardingGraph()
        else -> AppGraph()
    }
}
