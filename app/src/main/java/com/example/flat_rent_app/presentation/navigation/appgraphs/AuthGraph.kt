package com.example.flat_rent_app.presentation.navigation.appgraphs


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flat_rent_app.presentation.navigation.Routes
import com.example.flat_rent_app.presentation.screens.forgotpasswordscreen.ForgotPasswordScreen
import com.example.flat_rent_app.presentation.screens.loginscreen.LoginScreen
import com.example.flat_rent_app.presentation.screens.regscreen.RegisterScreen
import com.example.flat_rent_app.presentation.screens.startscreen.WelcomeScreen

@Composable
fun AuthGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.welcomeScreen.route
    ) {
        composable(Routes.welcomeScreen.route){
            WelcomeScreen(
                onLogin = { navController.navigate(Routes.authScreen.route)},
                onRegister = { navController.navigate(Routes.regScreen.route)  }
            )
        }

        composable(Routes.authScreen.route) {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onForgotPassword = { navController.navigate(Routes.forgotPasswordScreen.route) } // ← добавь
            )
        }

        composable(Routes.forgotPasswordScreen.route) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.regScreen.route) {
            RegisterScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
