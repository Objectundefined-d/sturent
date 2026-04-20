package com.example.flat_rent_app.presentation.screens.profiledetailscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flat_rent_app.domain.model.SwipeProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    profile: SwipeProfile,
    onBack: () -> Unit,
) {
    ProfileDetailContent(
        profile = profile,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailContent(
    profile: SwipeProfile,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Профиль", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                if (profile.photoUrl != null) {
                    AsyncImage(
                        model = profile.photoUrl,
                        contentDescription = "Фото профиля",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                                )
                            )
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = "${profile.name}, ${profile.age}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Местоположение", fontSize = 14.sp, color = Color.Gray)
                            Text(profile.city, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    HorizontalDivider()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Образование", fontSize = 14.sp, color = Color.Gray)
                            Text(profile.university, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    HorizontalDivider()
                    Column {
                        Text("О себе", fontSize = 14.sp, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Text(profile.description, fontSize = 16.sp, lineHeight = 24.sp)
                    }
                    HorizontalDivider()
                    Column {
                        Text("Ищет", fontSize = 14.sp, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Text(profile.lookingFor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ProfileDetailScreenPreview() {
    ProfileDetailContent(
        profile = SwipeProfile(
            uid = "preview_uid",
            name = "Имя",
            age = 22,
            city = "Город",
            university = "Вуз",
            description = "Описание",
            lookingFor = "Описание",
            photoUrl = null
        ),
        onBack = {}
    )
}
