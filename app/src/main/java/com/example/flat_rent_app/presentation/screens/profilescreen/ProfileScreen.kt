package com.example.flat_rent_app.presentation.screens.profilescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.flat_rent_app.presentation.viewmodel.profileviewmodel.ProfileViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.flat_rent_app.presentation.components.AppBottomBar
import com.example.flat_rent_app.util.BottomTabs

@Composable
fun ProfileScreen(
    onGoHome: () -> Unit,
    onGoChats: () -> Unit,
    onEditQuestionnaire: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState(initial = null)
    val userProfile by viewModel.userProfile.collectAsState()
    Scaffold(
        bottomBar = {
            AppBottomBar(
                selected = BottomTabs.PROFILE,
                onHome = onGoHome,
                onChats = onGoChats,
                onProfile = { }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                val mainPhotoUrl = userProfile?.photoSlots
                    ?.getOrNull(userProfile?.mainPhotoIndex ?: 0)
                    ?.fullUrl

                if (mainPhotoUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(mainPhotoUrl),
                        contentDescription = "Аватар",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "👤",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val displayName = when {
                !userProfile?.name.isNullOrBlank() -> userProfile?.name!!
                !user?.email.isNullOrBlank() -> user?.email?.substringBefore("@") ?: "Пользователь"
                else -> "Пользователь"
            }

            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            user?.email?.let { email ->
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 48.dp)
                )
            }

            Button(
                onClick = onEditQuestionnaire,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text("Анкета")
            }

            Button(
                onClick = viewModel::signOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Выйти")
            }
        }
    }
}