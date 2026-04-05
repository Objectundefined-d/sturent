package com.example.flat_rent_app.presentation.screens.profilescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.flat_rent_app.presentation.components.AppBottomBar
import com.example.flat_rent_app.presentation.viewmodel.profileviewmodel.ProfileViewModel
import com.example.flat_rent_app.util.BottomTabs
import android.content.res.Configuration
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    displayName: String,
    age: Int?,
    email: String?,
    photoUrls: List<String>,
    onEditQuestionnaire: () -> Unit,
    onSignOut: () -> Unit,
    onGoHome: () -> Unit,
    onGoChats: () -> Unit,
    onGoFavorites: () -> Unit,
    onGoSettings: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                actions = {
                    IconButton(onClick = onGoSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                selected = BottomTabs.PROFILE,
                onHome = onGoHome,
                onChats = onGoChats,
                onProfile = { },
                onFavorites = onGoFavorites
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val pagerState = rememberPagerState(pageCount = { maxOf(1, photoUrls.size) })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                if (photoUrls.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = photoUrls[page],
                            contentDescription = "Фото ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (photoUrls.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            photoUrls.indices.forEach { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (pagerState.currentPage == index)
                                                MaterialTheme.colorScheme.onPrimary
                                            else
                                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "$displayName, $age",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            email?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )
            }

            Button(
                onClick = onEditQuestionnaire,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text("Редактировать анкету")
            }

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Выйти")
            }
        }
    }
}

@Composable
fun ProfileScreen(
    onGoHome: () -> Unit,
    onGoChats: () -> Unit,
    onGoFavorites: () -> Unit,
    onEditQuestionnaire: () -> Unit,
    onGoSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState(initial = null)
    val userProfile by viewModel.userProfile.collectAsState()

    val slots = userProfile?.photoSlots ?: listOf(null, null, null)

    ProfileScreenContent(
        displayName = when {
            !userProfile?.name.isNullOrBlank() -> userProfile?.name!!
            !user?.email.isNullOrBlank() -> user?.email?.substringBefore("@") ?: "Пользователь"
            else -> "Пользователь"
        },
        age = userProfile?.age,
        email = user?.email,
        photoUrls = slots.mapNotNull { it?.fullUrl },
        onEditQuestionnaire = onEditQuestionnaire,
        onSignOut = viewModel::signOut,
        onGoHome = onGoHome,
        onGoChats = onGoChats,
        onGoFavorites = onGoFavorites,
        onGoSettings = onGoSettings
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ProfileScreenPreviewLight() {
    FlatrentappTheme {
        ProfileScreenContent(
            displayName = "Имя Фамилия",
            age = 22,
            email = "user@mail.com",
            photoUrls = listOf(
                "https://picsum.photos/seed/1/400/600",
                "https://picsum.photos/seed/2/400/600",
            ),
            onEditQuestionnaire = {},
            onSignOut = {},
            onGoHome = {},
            onGoChats = {},
            onGoFavorites = {},
            onGoSettings = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreviewDark() {
    FlatrentappTheme {
        ProfileScreenContent(
            displayName = "Имя Фамилия",
            age = 22,
            email = "user@mail.com",
            photoUrls = emptyList(),
            onEditQuestionnaire = {},
            onSignOut = {},
            onGoHome = {},
            onGoChats = {},
            onGoFavorites = {},
            onGoSettings = {}
        )
    }
}