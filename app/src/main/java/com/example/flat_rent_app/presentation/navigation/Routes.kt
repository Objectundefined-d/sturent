package com.example.flat_rent_app.presentation.navigation

sealed class Routes(val route: String) {
    object AuthScreen : Routes("auth")
    object RegScreen : Routes("registration")
    object HomeScreen : Routes("home")
    object ProfileScreen : Routes("profile")
    object OnbNameScreen : Routes("onboardingName")
    object OnbPhotoScreen : Routes("onboardingPhoto")
    object OnbPrefsScreen : Routes("onboardingPrefs")
    object OnbAboutScreen : Routes("onboardingAbout")
    object EditQuestionnaire : Routes("editquestionnaire")
    object WelcomeScreen : Routes("welcome")
    object ForgotPasswordScreen : Routes("forgot_password")
    object ChatsScreen : Routes("chats")
    object FavoritesScreen : Routes("favorites")
    object SettingsScreen: Routes("SettingsScreen")
    object ChatScreen : Routes("chat/{chatId}/{otherUid}") {
        fun create(chatId: String, otherUid: String) = "chat/$chatId/$otherUid"
    }
}