package com.example.flat_rent_app.presentation.navigation.appgraphs

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

    NavHost(
        navController = navController,
        startDestination = Routes.onbNameScreen.route
    ) {
        composable(Routes.onbNameScreen.route) {
            OnbNameScreen(
                onNext = { navController.navigate(Routes.onbPhotoScreen.route) },
                viewModel = vm
            )
        }

        composable(Routes.onbPhotoScreen.route) {
            OnbPhotoScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Routes.onbPrefsScreen.route) },
                viewModel = vm
            )
        }

        composable(Routes.onbPrefsScreen.route) {
            OnbPrefsScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Routes.onbAboutScreen.route) },
                viewModel = vm
            )
        }

        composable(Routes.onbAboutScreen.route) {
            OnbAboutScreen(
                onBack = { navController.popBackStack() },
                onFinish = { },
                viewModel = vm
            )
        }
    }
}