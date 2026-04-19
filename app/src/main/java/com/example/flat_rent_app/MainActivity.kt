package com.example.flat_rent_app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.flat_rent_app.domain.repository.ProfileRepository
import com.example.flat_rent_app.presentation.navigation.AppNav
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import com.example.flat_rent_app.presentation.theme.LocalThemeController
import com.example.flat_rent_app.presentation.theme.ThemeController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var profileRepository: ProfileRepository

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermission()

        val themePrefs = getSharedPreferences("notification_prefs", MODE_PRIVATE)
        var isDarkTheme by mutableStateOf(themePrefs.getBoolean("dark_theme", false))

        enableEdgeToEdge()
        setContent {
            FlatrentappTheme(darkTheme = isDarkTheme) {
                CompositionLocalProvider(
                    LocalThemeController provides ThemeController(
                        isDark = isDarkTheme,
                        setDark = { value ->
                            isDarkTheme = value
                            themePrefs.edit { putBoolean("dark_theme", value) }
                        }
                    )
                ) {
                    AppNav()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun saveFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            lifecycleScope.launch {
                profileRepository.saveFcmToken(token)
            }
        }
    }
}