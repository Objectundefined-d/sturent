package com.example.flat_rent_app.presentation.screens.profilescreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.flat_rent_app.presentation.components.AppBottomBar
import com.example.flat_rent_app.presentation.viewmodel.profileviewmodel.ProfileViewModel
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
    val context = LocalContext.current

    var activeSlot by remember { mutableIntStateOf(0) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadPhoto(context, activeSlot, it) }
    }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val slots = userProfile?.photoSlots ?: listOf(null, null, null)
            val mainIndex = userProfile?.mainPhotoIndex ?: 0

            Text(
                text = "Мои фото",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                (0..2).forEach { index ->
                    val photo = slots.getOrNull(index)
                    val isMain = mainIndex == index
                    val hasPhoto = photo?.fullUrl != null

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (isMain && hasPhoto) 2.dp else 0.dp,
                                    color = if (isMain && hasPhoto) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    activeSlot = index
                                    picker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (hasPhoto) {
                                AsyncImage(
                                    model = photo?.fullUrl,
                                    contentDescription = "Фото ${index + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                IconButton(
                                    onClick = { viewModel.deletePhoto(index) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(28.dp)
                                        .background(
                                            color = Color.Black.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Удалить",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Icon(
                                    imageVector = if (isMain) Icons.Default.Star else Icons.Outlined.StarOutline,
                                    contentDescription = if (isMain) "Главное фото" else "Сделать главным",
                                    tint = if (isMain) Color(0xFFFFD700) else Color.White,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                        .size(20.dp)
                                        .clickable {
                                            if (!isMain) viewModel.setMainPhoto(index)
                                        }
                                )
                            } else {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Добавить фото",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Text(
                            text = when {
                                isMain && hasPhoto -> "★ Главное"
                                hasPhoto -> "Нажми ★"
                                else -> "Добавить"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (isMain && hasPhoto)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                val mainPhotoUrl = slots.getOrNull(mainIndex)?.fullUrl

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
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val displayName = when {
                !userProfile?.name.isNullOrBlank() -> userProfile?.name!!
                !user?.email.isNullOrBlank() -> user?.email?.substringBefore("@") ?: "Пользователь"
                else -> "Пользователь"
            }

            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            user?.email?.let { email ->
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
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