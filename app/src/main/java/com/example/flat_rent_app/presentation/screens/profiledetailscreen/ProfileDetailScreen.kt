package com.example.flat_rent_app.presentation.screens.profiledetailscreen

import com.example.flat_rent_app.presentation.theme.TextSizes

import com.example.flat_rent_app.presentation.theme.Dimens

import android.content.res.Configuration
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flat_rent_app.R
import com.example.flat_rent_app.domain.model.SwipeProfile
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    profile: SwipeProfile,
    onBack: () -> Unit,
    onAddToSkipList: () -> Unit,
    onAddToBlackList: () -> Unit,
    onUnblock: () -> Unit,
    isBlocked: Boolean = false,
    mode: ProfileScreenMode
) {
    ProfileDetailContent(
        profile = profile,
        onBack = onBack,
        onAddToSkipList = onAddToSkipList,
        onAddToBlackList = onAddToBlackList,
        onUnblock = onUnblock,
        isBlocked = isBlocked,
        mode = mode
    )
}

enum class ProfileScreenMode { FROMCHAT, FROMSWIPE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailContent(
    profile: SwipeProfile,
    onBack: () -> Unit,
    onAddToSkipList: () -> Unit,
    onAddToBlackList: () -> Unit,
    onUnblock: () -> Unit,
    isBlocked: Boolean = false,
    mode: ProfileScreenMode
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.profile)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.dp16)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.dp300)
            ) {
                if (profile.photoUrl != null) {
                    AsyncImage(
                        model = profile.photoUrl,
                        contentDescription = stringResource(R.string.profile_photo),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimens.dp24),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = "${profile.name}, ${profile.age}",
                        fontSize = TextSizes.sp36,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.dp16),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.dp20),
                    verticalArrangement = Arrangement.spacedBy(Dimens.dp16)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Город",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(Dimens.dp24)
                        )
                        Spacer(modifier = Modifier.width(Dimens.dp12))
                        Column {
                            Text(
                                stringResource(R.string.location),
                                fontSize = TextSizes.sp14,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = profile.city,
                                fontSize = TextSizes.sp18,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = "Образование",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(Dimens.dp24)
                        )
                        Spacer(modifier = Modifier.width(Dimens.dp12))
                        Column {
                            Text(
                                stringResource(R.string.education),
                                fontSize = TextSizes.sp14,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = profile.university,
                                fontSize = TextSizes.sp18,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    HorizontalDivider()

                    Column {
                        Text(
                            stringResource(R.string.about_yourself),
                            fontSize = TextSizes.sp14,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Dimens.dp8))
                        Text(
                            text = profile.description,
                            fontSize = TextSizes.sp16,
                            lineHeight = TextSizes.sp24
                        )
                    }

                    HorizontalDivider()
                    Column {
                        Text(
                            stringResource(R.string.looking_for),
                            fontSize = TextSizes.sp14,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Dimens.dp8))
                        Text(
                            text = profile.lookingFor,
                            fontSize = TextSizes.sp16,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    HorizontalDivider()
                    Button(
                        onClick = when (mode) {
                            ProfileScreenMode.FROMCHAT if isBlocked -> onUnblock
                            ProfileScreenMode.FROMCHAT -> onAddToBlackList
                            else -> onAddToSkipList
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (mode) {
                                ProfileScreenMode.FROMCHAT if isBlocked -> MaterialTheme.colorScheme.secondary
                                ProfileScreenMode.FROMCHAT -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        Text(
                            when (mode) {
                                ProfileScreenMode.FROMCHAT if isBlocked -> stringResource(R.string.unblock)
                                ProfileScreenMode.FROMCHAT -> stringResource(R.string.ban)
                                else -> stringResource(R.string.dont_recommend)
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(Dimens.dp32))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ProfileDetailScreenPreviewLight() {
    FlatrentappTheme {
        ProfileDetailContent(
            profile = SwipeProfile(
                uid = "preview_uid",
                name = "Иван Иванов",
                age = 22,
                city = "Москва",
                university = "МГТУ им. Баумана",
                description = "Люблю спорт и путешествия",
                lookingFor = "Тихого соседа",
                photoUrl = null
            ),
            onBack = { },
            onAddToSkipList = { },
            onAddToBlackList = { },
            onUnblock = { },
            isBlocked = false,
            mode = ProfileScreenMode.FROMCHAT
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ProfileDetailScreenPreviewLightFromSwipe() {
    FlatrentappTheme {
        ProfileDetailContent(
            profile = SwipeProfile(
                uid = "preview_uid",
                name = "Иван Иванов",
                age = 22,
                city = "Москва",
                university = "МГТУ им. Баумана",
                description = "Люблю спорт и путешествия",
                lookingFor = "Тихого соседа",
                photoUrl = null
            ),
            onBack = { },
            onAddToSkipList = { },
            onAddToBlackList = { },
            onUnblock = { },
            isBlocked = false,
            mode = ProfileScreenMode.FROMSWIPE
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ProfileDetailScreenPreviewLightFromSwipeUnblock() {
    FlatrentappTheme {
        ProfileDetailContent(
            profile = SwipeProfile(
                uid = "preview_uid",
                name = "Иван Иванов",
                age = 22,
                city = "Москва",
                university = "МГТУ им. Баумана",
                description = "Люблю спорт и путешествия",
                lookingFor = "Тихого соседа",
                photoUrl = null
            ),
            onBack = { },
            onAddToSkipList = { },
            onAddToBlackList = { },
            onUnblock = { },
            isBlocked = true,
            mode = ProfileScreenMode.FROMCHAT
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileDetailScreenPreviewDarkFromChat() {
    FlatrentappTheme {
        ProfileDetailContent(
            profile = SwipeProfile(
                uid = "preview_uid",
                name = "Иван Иванов",
                age = 22,
                city = "Москва",
                university = "МГТУ им. Баумана",
                description = "Люблю спорт и путешествия",
                lookingFor = "Тихого соседа",
                photoUrl = null
            ),
            onBack = { },
            onAddToSkipList = { },
            onAddToBlackList = { },
            onUnblock = {},
            isBlocked = false,
            mode = ProfileScreenMode.FROMCHAT
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileDetailScreenPreviewDarkFromSwipe() {
    FlatrentappTheme {
        ProfileDetailContent(
            profile = SwipeProfile(
                uid = "preview_uid",
                name = "Иван Иванов",
                age = 22,
                city = "Москва",
                university = "МГТУ им. Баумана",
                description = "Люблю спорт и путешествия",
                lookingFor = "Тихого соседа",
                photoUrl = null
            ),
            onBack = { },
            onAddToSkipList = { },
            onAddToBlackList = { },
            onUnblock = { },
            isBlocked =  false,
            mode = ProfileScreenMode.FROMSWIPE
        )
    }
}
