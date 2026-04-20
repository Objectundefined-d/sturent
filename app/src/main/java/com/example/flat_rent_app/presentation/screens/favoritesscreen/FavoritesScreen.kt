package com.example.flat_rent_app.presentation.screens.favoritesscreen

import com.example.flat_rent_app.presentation.theme.TextSizes

import com.example.flat_rent_app.presentation.theme.Dimens

import android.content.res.Configuration
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.flat_rent_app.presentation.screens.profiledetailscreen.ProfileScreenMode
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import com.example.flat_rent_app.presentation.viewmodel.blacklistviewmodel.BlackListViewModel

private const val SWIPE_HINT_THRESHOLD = 20f
private const val SWIPE_OFFSCREEN_OFFSET = 2000f
private const val SWIPE_ANIMATION_MS = 300
private val SwipeLikeBackground = Color(0xFF38D986)
private val SwipeNopeBackground = Color(0xFFFF4458)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onGoHome: () -> Unit,
    onGoProfile: () -> Unit,
    onGoChats: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
    blackListViewModel: BlackListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val blackListState by blackListViewModel.state.collectAsStateWithLifecycle()

    if (state.selectedProfile != null) {

        LaunchedEffect(state.selectedProfile) {
            state.selectedProfile?.uid?.let { blackListViewModel.checkIsBlocked(it) }
        }

        ProfileDetailScreen(
            profile = state.selectedProfile!!.toSwipeProfile(),
            onBack = viewModel::closeProfile,
            onAddToSkipList = { },
            onAddToBlackList = {
                state.selectedProfile?.uid?.let { blackListViewModel.blockUser(it) }
            },
            onUnblock = {
                state.selectedProfile?.uid?.let { blackListViewModel.unblockUser(it) }
            },
            isBlocked = blackListState.profileBlocked,
            mode = ProfileScreenMode.FROMCHAT
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

    FavoriteScreenContent(
        error = state.error,
        isLoading = state.isLoading,
        profiles = state.profiles,
        onGoHome = onGoHome,
        onGoProfile = onGoProfile,
        onGoChats = onGoChats,
        retry = viewModel::retry,
        openProfile = viewModel::openProfile,
        swipeLeft = viewModel::swipeLeft,
        swipeRight = viewModel::swipeRight
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreenContent(
    error: String?,
    isLoading: Boolean,
    profiles: List<UserProfile>,
    onGoHome: () -> Unit,
    onGoProfile: () -> Unit,
    onGoChats: () -> Unit,
    retry: () -> Unit,
    openProfile: (UserProfile) -> Unit = { },
    swipeLeft: (String) -> Unit = { },
    swipeRight: (String) -> Unit = { }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.favorites),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
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
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                profiles.isEmpty() -> Text(
                    text = stringResource(R.string.no_favorites),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center)
                )

                error != null -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.smth_wrong),
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = TextSizes.sp14)
                    Button(onClick = { retry() }) { Text(text = stringResource(R.string.repeat)) }
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimens.dp16),
                    verticalArrangement = Arrangement.spacedBy(Dimens.dp12)
                ) {
                    items(profiles) { profile ->
                        FavoriteCard(
                            profile = profile,
                            onClick = { openProfile(profile) },
                            onSwipeLeft = { swipeLeft(profile.uid) },
                            onSwipeRight = { swipeRight(profile.uid) }
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
    val threshold = with(density) { Dimens.dp100.toPx() }

    Box(modifier = Modifier.fillMaxWidth()) {

        if (offsetX.value > SWIPE_HINT_THRESHOLD) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(Dimens.dp16))
                    .background(
                        SwipeLikeBackground.copy(
                            alpha = (offsetX.value / threshold).coerceIn(
                                0f,
                                1f
                            )
                        )
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(start = Dimens.dp24)
                        .size(Dimens.dp32)
                )
            }
        } else if (offsetX.value < -SWIPE_HINT_THRESHOLD) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(Dimens.dp16))
                    .background(
                        SwipeNopeBackground.copy(
                            alpha = (-offsetX.value / threshold).coerceIn(
                                0f,
                                1f
                            )
                        )
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(end = Dimens.dp24)
                        .size(Dimens.dp32)
                )
            }
        }

        Card(
            onClick = onClick,
            shape = RoundedCornerShape(Dimens.dp16),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
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
                                        offsetX.animateTo(SWIPE_OFFSCREEN_OFFSET, tween(SWIPE_ANIMATION_MS))
                                        onSwipeRight()
                                        offsetX.snapTo(0f)
                                    }

                                    offsetX.value < -threshold -> {
                                        offsetX.animateTo(-SWIPE_OFFSCREEN_OFFSET, tween(SWIPE_ANIMATION_MS))
                                        onSwipeLeft()
                                        offsetX.snapTo(0f)
                                    }

                                    else -> offsetX.animateTo(0f, tween(SWIPE_ANIMATION_MS))
                                }
                            }
                        }
                    )
                }
        ) {
            Row(
                modifier = Modifier.padding(Dimens.dp12),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.dp12)
            ) {
                AsyncImage(
                    model = profile.photoSlots.getOrNull(profile.mainPhotoIndex)?.fullUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(Dimens.dp60)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = stringResource(
                            R.string.user_name_age_comma,
                            profile.name,
                            profile.age?.toString() ?: ""
                        ),
                        fontSize = TextSizes.sp16,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(text = profile.city, fontSize = TextSizes.sp14, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true, name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PreviewFavoritesLight() {
    FlatrentappTheme {
        FavoriteScreenContent(
            error = null,
            isLoading = false,
            profiles = emptyList(),
            onGoHome = {},
            onGoProfile = {},
            onGoChats = {},
            retry = {}
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewFavoritesDark() {
    FlatrentappTheme {
        FavoriteScreenContent(
            error = null,
            isLoading = false,
            profiles = emptyList(),
            onGoHome = {},
            onGoProfile = {},
            onGoChats = {},
            retry = {}
        )
    }
}


