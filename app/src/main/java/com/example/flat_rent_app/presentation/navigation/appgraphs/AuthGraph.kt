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
        startDestination = Routes.WelcomeScreen.route
    ) {
        composable(Routes.WelcomeScreen.route){
            WelcomeScreen(
                onLogin = { navController.navigate(Routes.AuthScreen.route)},
                onRegister = { navController.navigate(Routes.RegScreen.route)  }
            )
        }

        composable(Routes.AuthScreen.route) {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onForgotPassword = { navController.navigate(Routes.ForgotPasswordScreen.route) }
            )
        }

        composable(Routes.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.RegScreen.route) {
            RegisterScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
