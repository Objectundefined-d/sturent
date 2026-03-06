package com.example.flat_rent_app.presentation.screens.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.runtime.remember
import com.example.flat_rent_app.domain.model.SwipeProfile
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.flat_rent_app.domain.model.BottomTab
import com.example.flat_rent_app.presentation.components.AppBottomBar
import com.example.flat_rent_app.presentation.viewmodel.mainviewmodel.MainViewModel
import com.example.flat_rent_app.presentation.screens.profiledetailscreen.ProfileDetailScreen
import com.example.flat_rent_app.util.BottomTabs


@Composable
fun SwipeableProfileCard(
    profile: SwipeProfile,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y

                        if (abs(offsetX) > 300f) {
                            if (offsetX > 0) {
                                onSwipeRight()
                            } else {
                                onSwipeLeft()
                            }
                            offsetX = 0f
                            offsetY = 0f
                        }
                    },
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            }
            .offset(x = offsetX.dp, y = offsetY.dp)
    ) {
        ProfileCard(
            name = profile.name,
            age = profile.age,
            city = profile.city,
            university = profile.university,
            description = profile.description,
            lookingFor = profile.lookingFor,
            photoUrl = profile.photoUrl
        )

        if (abs(offsetX) > 50f) {
            val color = if (offsetX > 0) Color.Green else Color.Red
            val icon = if (offsetX > 0) Icons.Default.Favorite else Icons.Default.Close
            val text = if (offsetX > 0) "LIKE" else "NOPE"

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = text,
                        tint = color,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = text,
                        color = color,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileCard(
    name: String,
    age: Int,
    city: String,
    university: String,
    description: String,
    lookingFor: String,
    photoUrl: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Фото профиля",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
//                                    Color(0xFF6A11CB),
                                    Color(0xFF2575FC)
                                )
                            )
                        )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$name, $age",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Город",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = city,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = "Университет",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = university,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = description,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = lookingFor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AllViewedView(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Text("Все профили просмотрены", style = MaterialTheme.typography.headlineSmall)
        Text("Загляните позже или обновите список", color = Color.Gray)
        Button(onClick = onRetry) {
            Text("Обновить")
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreenContent(
    profiles: List<SwipeProfile>,
    currentIndex: Int,
    isLoading: Boolean,
    error: String?,
    showAllViewed: Boolean,
    swipeRight: () -> Unit,
    swipeLeft: () -> Unit,
    openProfileDetails: () -> Unit,
    onGoChats: () -> Unit,
    onGoProfile: () -> Unit,
    retry: () -> Unit
) {
    Scaffold(
        bottomBar = {
            AppBottomBar(
                selected = BottomTabs.HOME,
                onHome = { },
                onChats = onGoChats,
                onProfile = onGoProfile
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingView()
                        }
                    }

                    error != null -> {
                        ErrorView(
                            error = error,
                            onRetry = retry
                        )
                    }

                    profiles.isEmpty() -> {
                        EmptyView()
                    }

                    showAllViewed -> {
                        AllViewedView(onRetry = retry)
                    }

                    else -> {
                        val currentProfile = profiles[currentIndex]
                        SwipeableProfileCard(
                            profile = currentProfile,
                            onSwipeLeft = swipeLeft,
                            onSwipeRight = swipeRight,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isLoading && error == null && profiles.isNotEmpty() && !showAllViewed) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FloatingActionButton(
                            onClick = swipeLeft,
                            containerColor = Color(0xFFEEEEEE),
                            contentColor = Color.Black,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Text("Нет", fontWeight = FontWeight.Bold)
                        }

                        FloatingActionButton(
                            onClick = { openProfileDetails() },
                            containerColor = Color(0xFFEEEEEE),
                            contentColor = Color.Black,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Text("Инфо", fontWeight = FontWeight.Bold)
                        }

                        FloatingActionButton(
                            onClick = swipeRight,
                            containerColor = Color(0xFFEEEEEE),
                            contentColor = Color.Black,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            Text("Да", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }


}
@Composable
fun MainScreen(
    onGoProfile: () -> Unit,
    onGoChats: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    if (state.showProfileDetails) {
        val profile = state.selectedProfile
        if (profile != null) {
            ProfileDetailScreen(
                profile = profile,
                onBack = viewModel::closeProfileDetails
            )
            return
        }
    }

    MainScreenContent(
        profiles = state.profiles,
        currentIndex = state.currentIndex,
        isLoading = state.isLoading,
        error = state.error,
        showAllViewed = state.showAllViewed,
        swipeRight = viewModel::swipeRight,
        swipeLeft = viewModel::swipeLeft,
        openProfileDetails = viewModel::openProfileDetails,
        onGoChats = onGoChats,
        onGoProfile = onGoProfile,
        retry = viewModel::retry
    )

}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
fun PreviewMainScreen(
    onGoChats: () -> Unit = {},
    onGoProfile: () -> Unit = {},
) {
    MainScreenContent(
        profiles = emptyList<SwipeProfile>(),
        currentIndex = -1,
        isLoading = false,
        error = null,
        showAllViewed = false,
        swipeRight = {},
        swipeLeft = {},
        openProfileDetails = {},
        onGoChats= onGoChats,
        onGoProfile = onGoProfile,
        retry = {}
    )
}

@Composable
fun LoadingView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator()
        Text("Загрузка профилей...", color = Color.Gray)
    }
}

@Composable
fun ErrorView(error: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Text("Ошибка", style = MaterialTheme.typography.headlineSmall)
        Text(error, color = Color.Red)
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}

@Composable
fun EmptyView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Нет доступных профилей", color = Color.Gray)
        Text("Попробуйте позже", color = Color.Gray, fontSize = 14.sp)
    }
}