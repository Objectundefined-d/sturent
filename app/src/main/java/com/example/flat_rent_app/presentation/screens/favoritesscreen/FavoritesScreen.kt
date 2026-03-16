package com.example.flat_rent_app.presentation.screens.favoritesscreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.presentation.components.AppBottomBar
import com.example.flat_rent_app.presentation.screens.mainscreen.MatchScreen
import com.example.flat_rent_app.presentation.screens.profiledetailscreen.ProfileDetailScreen
import com.example.flat_rent_app.presentation.viewmodel.favoritesviewmodel.FavoritesViewModel
import com.example.flat_rent_app.util.BottomTabs
import kotlinx.coroutines.launch
import com.example.flat_rent_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onGoHome: () -> Unit,
    onGoProfile: () -> Unit,
    onGoChats: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.selectedProfile != null) {
        ProfileDetailScreen(
            profile = state.selectedProfile!!.toSwipeProfile(),
            onBack = viewModel::closeProfile
        )
        return
    }

    if (state.matchChatId != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissMatch() }
        ) {
            MatchScreen(
                onSendMessage = { viewModel.dismissMatch() },
                onContinue = { viewModel.dismissMatch() }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.favorites)) })
        },
        bottomBar = {
            AppBottomBar(
                selected = BottomTabs.FAVORITES,
                onHome = onGoHome,
                onChats = onGoChats,
                onProfile = onGoProfile,
                onFavorites = { }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                state.profiles.isEmpty() -> Text(
                    text = stringResource(R.string.no_favorites),
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )

                state.error != null -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.smth_wrong), color = Color.White)
                    // убрать !!
                    Text(text = state.error!!, color = Color.Gray, fontSize = 14.sp)
                    Button(onClick = { viewModel.retry() }) { Text(text = stringResource(R.string.repeat)) }
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.profiles) { profile ->
                        FavoriteCard(
                            profile = profile,
                            onClick = { viewModel.openProfile(profile) },
                            onSwipeLeft = { viewModel.swipeLeft(profile.uid) },
                            onSwipeRight = { viewModel.swipeRight(profile.uid) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteCard(
    profile: UserProfile,
    onClick: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val threshold = with(density) { 100.dp.toPx() }

    Box(modifier = Modifier.fillMaxWidth()) {

        if (offsetX.value > 20f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF38D986).copy(alpha = (offsetX.value / threshold).coerceIn(0f, 1f))),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .size(32.dp)
                )
            }
        } else if (offsetX.value < -20f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFF4458).copy(alpha = (-offsetX.value / threshold).coerceIn(0f, 1f))),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .size(32.dp)
                )
            }
        }

        Card(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { translationX = offsetX.value }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, drag ->
                            change.consume()
                            scope.launch { offsetX.snapTo(offsetX.value + drag.x) }
                        },
                        onDragEnd = {
                            scope.launch {
                                when {
                                    offsetX.value > threshold -> {
                                        offsetX.animateTo(2000f, tween(300))
                                        onSwipeRight()
                                        offsetX.snapTo(0f)
                                    }
                                    offsetX.value < -threshold -> {
                                        offsetX.animateTo(-2000f, tween(300))
                                        onSwipeLeft()
                                        offsetX.snapTo(0f)
                                    }
                                    else -> offsetX.animateTo(0f, tween(300))
                                }
                            }
                        }
                    )
                }
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = profile.photoSlots.getOrNull(profile.mainPhotoIndex)?.fullUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp).clip(CircleShape)
                )
                Column {
                    Text(
                        text = "${profile.name}, ${profile.age ?: ""}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(text = profile.city, fontSize = 14.sp, color = Color.Gray)
                }
            }
        }
    }
}


