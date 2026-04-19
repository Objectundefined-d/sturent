package com.example.flat_rent_app.presentation.screens.blacklistscreen

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.flat_rent_app.R
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.presentation.screens.profiledetailscreen.ProfileDetailScreen
import com.example.flat_rent_app.presentation.screens.profiledetailscreen.ProfileScreenMode
import com.example.flat_rent_app.presentation.viewmodel.blacklistviewmodel.BlackListViewModel
import kotlinx.coroutines.launch

@Composable
fun BlackListScreen(
    onBack: () -> Unit,
    viewModel: BlackListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.selectedProfile != null) {

        LaunchedEffect(state.selectedProfile) {
            state.selectedProfile?.uid?.let { viewModel.checkIsBlocked(it) }
        }

        ProfileDetailScreen(
            profile = state.selectedProfile!!.toSwipeProfile(),
            onBack = viewModel::closeProfile,
            onAddToSkipList = { },
            onAddToBlackList = {
                state.selectedProfile?.uid?.let { viewModel.blockUser(it) }
            },
            onUnblock = {
                state.selectedProfile?.uid?.let { viewModel.unblockUser(it) }
            },
            isBlocked = state.profileBlocked,
            mode = ProfileScreenMode.FROMCHAT
        )
        return
    }

    BlackListScreenContent(
        error = state.error,
        isLoading = state.isLoading,
        profiles = state.profiles,
        onBack = onBack,
        retry = viewModel::retry,
        unblockUser = viewModel::unblockUser,
        openProfile = viewModel::openProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlackListScreenContent(
    error: String?,
    isLoading: Boolean,
    profiles: List<UserProfile>,
    onBack: () -> Unit,
    retry: () -> Unit,
    unblockUser: (String) -> Unit,
    openProfile: (UserProfile) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.black_list)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
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
                    text = stringResource(R.string.no_blocked),
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
                    Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                    Button(onClick = { retry() }) { Text(text = stringResource(R.string.repeat)) }
                }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(profiles) { profile ->
                        BlockedPersonCard(
                            profile = profile,
                            onClick = { openProfile(profile) },
                            onSwipeLeft = { unblockUser(profile.uid) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BlockedPersonCard(
    profile: UserProfile,
    onClick: () -> Unit,
    onSwipeLeft: () -> Unit,
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val threshold = with(density) { 100.dp.toPx() }


    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (offsetX.value < -20f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Color(0xFFFF4458).copy(
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
                        .padding(end = 24.dp)
                        .size(32.dp)
                )
            }
        }
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
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
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = stringResource(
                            R.string.user_name_age_comma,
                            profile.name,
                            profile.age?.toString() ?: ""
                        ),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(text = profile.city, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}






@Preview(name = "Light mode - list of blocked users", showBackground = true)
@Composable
fun PreviewBlackListContentList() {
    MaterialTheme {
        BlackListScreenContent(
            error = null,
            isLoading = false,
            profiles = listOf(UserProfile(
                uid = "1",
                name = "Анна",
                age = 25,
                city = "Москва",
                photoSlots = listOf(
                    null
                ),
                mainPhotoIndex = 0
            ), UserProfile(
                uid = "2",
                name = "Дмитрий",
                age = 30,
                city = "Санкт-Петербург",
                photoSlots = listOf(
                    null
                ),
                mainPhotoIndex = 0
            ), UserProfile(
                uid = "3",
                name = "Елена",
                age = 28,
                city = "Казань",
                photoSlots = listOf(
                    null
                ),
                mainPhotoIndex = 0
            )),
            onBack = {},
            retry = {},
            unblockUser = {},
            openProfile = {}
        )
    }
}

@Preview(name = "Dark mode - list of blocked users", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewBlackListContentListDark() {
    MaterialTheme {
        BlackListScreenContent(
            error = null,
            isLoading = false,
            profiles = listOf(UserProfile(
                uid = "1",
                name = "Анна",
                age = 25,
                city = "Москва",
                photoSlots = listOf(
                    null
                ),
                mainPhotoIndex = 0
            ), UserProfile(
                uid = "2",
                name = "Дмитрий",
                age = 30,
                city = "Санкт-Петербург",
                photoSlots = listOf(
                    null
                ),
                mainPhotoIndex = 0
            ), UserProfile(
                uid = "3",
                name = "Елена",
                age = 28,
                city = "Казань",
                photoSlots = listOf(
                    null
                ),
                mainPhotoIndex = 0
            )),
            onBack = {},
            retry = {},
            unblockUser = {},
            openProfile = {}
        )
    }
}

@Preview(name = "Light mode - empty state", showBackground = true)
@Composable
fun PreviewBlackListContentEmpty() {
    MaterialTheme {
        BlackListScreenContent(
            error = null,
            isLoading = false,
            profiles = emptyList(),
            onBack = {},
            retry = {},
            unblockUser = {},
            openProfile = {}
        )
    }
}

@Preview(name = "Dark mode - empty state", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewBlackListContentEmptyDark() {
    MaterialTheme {
        BlackListScreenContent(
            error = null,
            isLoading = false,
            profiles = emptyList(),
            onBack = {},
            retry = {},
            unblockUser = {},
            openProfile = {}
        )
    }
}

@Preview(name = "Light mode - loading", showBackground = true)
@Composable
fun PreviewBlackListContentLoading() {
    MaterialTheme {
        BlackListScreenContent(
            error = null,
            isLoading = true,
            profiles = emptyList(),
            onBack = {},
            retry = {},
            unblockUser = {},
            openProfile = {}
        )
    }
}

@Preview(name = "Dark mode - loading", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewBlackListContentLoadingDark() {
    MaterialTheme {
        BlackListScreenContent(
            error = null,
            isLoading = true,
            profiles = emptyList(),
            onBack = {},
            retry = {},
            unblockUser = {},
            openProfile = {}
        )
    }
}

@Preview(name = "Light mode - error", showBackground = true)
@Composable
fun PreviewBlackListContentError() {
    MaterialTheme {
        BlackListScreenContent(
            error = "Network error occurred",
            isLoading = false,
            profiles = emptyList(),
            onBack = {},
            retry = {},
            unblockUser = {},
            openProfile = {}
        )
    }
}

@Preview(name = "Dark mode - error", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewBlackListContentErrorDark() {
    MaterialTheme {
        BlackListScreenContent(
            error = "Network error occurred",
            isLoading = false,
            profiles = emptyList(),
            onBack = {},
            retry = {},
            unblockUser = {},
            openProfile = {}
        )
    }
}