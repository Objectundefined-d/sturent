package com.example.flat_rent_app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.flat_rent_app.domain.repository.ProfileRepository
import com.example.flat_rent_app.presentation.navigation.AppNav
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
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

        enableEdgeToEdge()
        setContent {
            FlatrentappTheme {
                AppNav()
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
