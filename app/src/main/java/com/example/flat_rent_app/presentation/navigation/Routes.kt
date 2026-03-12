package com.example.flat_rent_app.presentation.navigation

sealed class Routes(val route: String) {
    object authScreen : Routes("auth")
    object regScreen : Routes("registration")
    object homeScreen : Routes("home")
    object profileScreen : Routes("profile")
    object formScreen : Routes("form")
    object onbNameScreen : Routes("onboardingName")

    object onbPhotoScreen : Routes("onboardingPhoto")

    object onbPrefsScreen : Routes("onboardingPrefs")

    object onbAboutScreen : Routes("onboardingAbout")
    object EditQuestionnaire : Routes("editquestionnaire")
    object welcomeScreen : Routes("welcome")

    object forgotPasswordScreen : Routes("forgot_password")

    object ChatsScreen : Routes("chats")
    object ChatScreen : Routes("chat/{chatId}/{otherUid}") {
        fun create(chatId: String, otherUid: String) = "chat/$chatId/$otherUid"
    }
}