package com.example.flat_rent_app.presentation.navigation.appgraphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flat_rent_app.presentation.navigation.Routes
import com.example.flat_rent_app.presentation.screens.onboarding.OnbAboutScreen
import com.example.flat_rent_app.presentation.screens.onboarding.OnbNameScreen
import com.example.flat_rent_app.presentation.screens.onboarding.OnbPhotoScreen
import com.example.flat_rent_app.presentation.screens.onboarding.OnbPrefsScreen
import com.example.flat_rent_app.presentation.viewmodel.onboarding.OnboardingViewModel

@Composable
fun OnboardingGraph() {
    val navController = rememberNavController()
    val vm: OnboardingViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        vm.resetState()
    }

    NavHost(
        navController = navController,
        startDestination = Routes.OnbNameScreen.route
    ) {
        composable(Routes.OnbNameScreen.route) {
            OnbNameScreen(
                onNext = { navController.navigate(Routes.OnbPhotoScreen.route) },
                viewModel = vm
            )
        }

        composable(Routes.OnbPhotoScreen.route) {
            OnbPhotoScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Routes.OnbPrefsScreen.route) },
                viewModel = vm
            )
        }

        composable(Routes.OnbPrefsScreen.route) {
            OnbPrefsScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Routes.OnbAboutScreen.route) },
                viewModel = vm
            )
        }

        composable(Routes.OnbAboutScreen.route) {
            OnbAboutScreen(
                onBack = { navController.popBackStack() },
                onFinish = { },
                viewModel = vm
            )
        }
    }
}