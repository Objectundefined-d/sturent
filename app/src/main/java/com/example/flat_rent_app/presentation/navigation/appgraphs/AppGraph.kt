package com.example.flat_rent_app.presentation.navigation.appgraphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flat_rent_app.presentation.navigation.Routes
import com.example.flat_rent_app.presentation.screens.chatscreen.ChatScreen
import com.example.flat_rent_app.presentation.screens.chatsscreen.ChatsScreen
import com.example.flat_rent_app.presentation.screens.mainscreen.MainScreen
import com.example.flat_rent_app.presentation.screens.profilescreen.ProfileScreen
import com.example.flat_rent_app.presentation.screens.editquestionnairescreen.EditQuestionnaireScreen
import com.example.flat_rent_app.presentation.screens.favoritesscreen.FavoritesScreen
import com.example.flat_rent_app.presentation.viewmodel.mainviewmodel.MainViewModel
import com.example.flat_rent_app.presentation.screens.settingsscreen.SettingsScreen

@Composable
fun AppGraph() {
    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = Routes.HomeScreen.route
    ) {
        composable(Routes.HomeScreen.route) {
            val backStackEntry = remember(it) { it }
            val mainViewModel: MainViewModel = hiltViewModel(backStackEntry)

            MainScreen(
                onGoProfile = { navController.navigate(Routes.ProfileScreen.route) },
                onGoChats = { navController.navigate(Routes.ChatsScreen.route) },
                onGoFavorites = { navController.navigate(Routes.FavoritesScreen.route) },
                onOpenChat = { chatId, otherUid ->
                    navController.navigate(Routes.ChatScreen.create(chatId, otherUid))
                },
                viewModel = mainViewModel
            )
        }

        composable(Routes.ProfileScreen.route) {
            ProfileScreen(
                onGoHome = {
                    navController.popBackStack(
                        route = Routes.HomeScreen.route,
                        inclusive = false
                    )
                },
                onGoChats = { navController.navigate(Routes.ChatsScreen.route) },
                onGoFavorites = { navController.navigate(Routes.FavoritesScreen.route) },
                onEditQuestionnaire = { navController.navigate(Routes.EditQuestionnaire.route) },
                onGoSettings = { navController.navigate(Routes.SettingsScreen.route) }
            )
        }

        composable(Routes.EditQuestionnaire.route) {
            EditQuestionnaireScreen(
                onBack = { navController.popBackStack() },
                onSaveComplete = { navController.popBackStack() }
            )
        }

        composable(Routes.SettingsScreen.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }



        composable(Routes.ChatsScreen.route) {
            ChatsScreen(onOpenChat = { chatId, otherUid ->
                navController.navigate(Routes.ChatScreen.create(chatId, otherUid))
            },
                onGoFavorites = { navController.navigate(Routes.FavoritesScreen.route) },
                onGoHome = {
                    navController.popBackStack(
                        route = Routes.HomeScreen.route,
                        inclusive = false
                    )
                },
                onGoProfile = { navController.navigate(Routes.ProfileScreen.route) },)
        }

        composable(Routes.FavoritesScreen.route) {
            FavoritesScreen(
                onGoHome = {
                    navController.popBackStack(
                        route = Routes.HomeScreen.route,
                        inclusive = false
                    )
                },
                onGoProfile = { navController.navigate(Routes.ProfileScreen.route) },
                onGoChats = { navController.navigate(Routes.ChatsScreen.route)}
            )
        }

        composable(
            route = Routes.ChatScreen.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("otherUid") { type = NavType.StringType }
            )
        ) {
            ChatScreen(onBack = { navController.popBackStack() })
        }
    }
}
