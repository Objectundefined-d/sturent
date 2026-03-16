package com.example.flat_rent_app.presentation.screens.mainscreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.flat_rent_app.domain.model.SwipeProfile
import com.example.flat_rent_app.presentation.components.AppBottomBar
import com.example.flat_rent_app.presentation.screens.profiledetailscreen.ProfileDetailScreen
import com.example.flat_rent_app.presentation.viewmodel.mainviewmodel.MainViewModel
import com.example.flat_rent_app.util.BottomTabs
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import com.example.flat_rent_app.presentation.viewmodel.mainviewmodel.MainScreenState
import com.example.flat_rent_app.util.Constants

private val LikeGreen = Color(0xFF38D986)
private val NopeRed = Color(0xFFFF4458)
private val CardShadow = Color(0x22000000)

@Composable
fun SwipeableProfileCard(
    profile: SwipeProfile,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val threshold = with(density) { 110.dp.toPx() }

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = offsetX.value
                rotationZ = (offsetX.value / 25f).coerceIn(-22f, 22f)
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, drag ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + drag.x)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > threshold -> {
                                    offsetX.animateTo(2400f, tween(320))
                                    onSwipeRight()
                                    offsetX.snapTo(0f)
                                }

                                offsetX.value < -threshold -> {
                                    offsetX.animateTo(-2400f, tween(320))
                                    onSwipeLeft()
                                    offsetX.snapTo(0f)
                                }

                                else -> {
                                    offsetX.animateTo(0f, tween(380))
                                }
                            }
                        }
                    }
                )
            }
    ) {
        ProfileCard(
            name = profile.name,
            age = profile?.age,
            city = profile.city,
            university = profile.university,
            description = profile.description,
            photoUrl = profile.photoUrl,
        )
    }
}


@Composable
fun ProfileCard(
    name: String,
    age: Int?,
    city: String,
    university: String,
    description: String,
    photoUrl: String? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = CardShadow)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1C1C1E))
    ) {
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2C2C2E), Color(0xFF1C1C1E))
                        )
                    )
            ) {
                Text(
                    text = "👤",
                    fontSize = 96.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0xCC000000)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$name, $age",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(city, color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = university,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    maxLines = 1
                )
            }

            if (description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onInfo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloatingActionButton(
            onClick = onSwipeLeft,
            containerColor = Color.White,
            contentColor = NopeRed,
            shape = CircleShape,
            modifier = Modifier.size(64.dp),
            elevation = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Пропустить",
                modifier = Modifier.size(30.dp)
            )
        }

        FloatingActionButton(
            onClick = onInfo,
            containerColor = Color.White,
            contentColor = Color(0xFF636366),
            shape = CircleShape,
            modifier = Modifier.size(48.dp),
            elevation = FloatingActionButtonDefaults.elevation(4.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = "Инфо", modifier = Modifier.size(22.dp))
        }

        FloatingActionButton(
            onClick = onSwipeRight,
            containerColor = LikeGreen,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(64.dp),
            elevation = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = "Нравится",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun LoadingView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        CircularProgressIndicator(color = LikeGreen)
        Text("Загружаем...", color = Color.Gray, fontSize = 15.sp)
    }
}

@Composable
fun ErrorView(error: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Text("Что-то пошло не так", style = MaterialTheme.typography.headlineSmall)
        Text(error, color = Color.Gray, fontSize = 14.sp)
        Button(onClick = onRetry) { Text("Попробовать снова") }
    }
}

@Composable
fun EmptyView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Анкеты не найдены", color = Color.Gray, fontSize = 16.sp)
        Text("Попробуйте позже", color = Color.LightGray, fontSize = 14.sp)
    }
}

@Composable
fun AllViewedView(onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Text("Все просмотрено", style = MaterialTheme.typography.headlineSmall)
        Text("Загляните позже — появятся новые анкеты", color = Color.Gray, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Обновить") }
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
    retry: () -> Unit,
    selectedUniversityFilter: String,
    onOpenFilters: () -> Unit
) {
    val showCards = !isLoading && error == null && profiles.isNotEmpty() && !showAllViewed

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Лента") },
                actions = {
                    FilterButton(
                        onClick = onOpenFilters
                    )
                }
            )
        },
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> LoadingView()
                    error != null -> ErrorView(error, retry)
                    profiles.isEmpty() -> EmptyView()
                    showAllViewed -> AllViewedView(retry)
                    else -> {
                        val nextIndex = currentIndex + 1
                        if (nextIndex < profiles.size) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        scaleX = 0.93f
                                        scaleY = 0.93f
                                        translationY = 20f
                                    }
                            ) {
                                ProfileCard(
                                    name = profiles[nextIndex].name,
                                    age = profiles[nextIndex].age,
                                    city = profiles[nextIndex].city,
                                    university = profiles[nextIndex].university,
                                    description = profiles[nextIndex].description,
                                    photoUrl = profiles[nextIndex].photoUrl
                                )
                            }
                        }

                        SwipeableProfileCard(
                            profile = profiles[currentIndex],
                            onSwipeLeft = swipeLeft,
                            onSwipeRight = swipeRight,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            if (showCards) {
                ActionButtons(
                    onSwipeLeft = swipeLeft,
                    onSwipeRight = swipeRight,
                    onInfo = openProfileDetails
                )
            }
        }
    }
}

@Composable
fun FilterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            Icons.Default.FilterAlt,
            contentDescription = "Открыть фильтры"
        )
    }
}

@Composable
fun MainScreen(
    onGoProfile: () -> Unit,
    onGoChats: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.showFilters) {
        FiltersScreen(
            state = state,
            onClose = { viewModel.closeFilters() },
            onApplyFilters = { university, gender, minAge, maxAge ->
                viewModel.applyFilters(university, gender, minAge, maxAge)
                viewModel.closeFilters()
            }
        )
        return
    }

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
        retry = viewModel::retry,
        selectedUniversityFilter = state.selectedUniversityFilter,
        onOpenFilters = { viewModel.openFilters() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    state: MainScreenState,
    onClose: () -> Unit,
    onApplyFilters: (university: String, gender: String, minAge: Int, maxAge: Int) -> Unit
) {
    var selectedUniversity by remember { mutableStateOf(state.selectedUniversityFilter) }
    var selectedGender by remember { mutableStateOf(state.selectedGenderFilter) }
    var ageRange by remember {
        mutableStateOf(
            state.ageFilterMin.toFloat()..state.ageFilterMax.toFloat()
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фильтры") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("ВУЗ", fontWeight = FontWeight.Bold, color = Color.Black)
            DropdownMenuForUniversity(
                selectedUniversity = selectedUniversity,
                onSelect = { selectedUniversity = it }
            )

            Text("Пол", fontWeight = FontWeight.Bold, color = Color.Black)
            GenderRadioGroup(
                selectedGender = selectedGender,
                onSelect = { selectedGender = it }
            )

            Text(
                text = "Возраст: ${ageRange.start.toInt()}–${ageRange.endInclusive.toInt()}",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            AgeRangeSlider(
                ageRange = ageRange,
                onRangeChange = { ageRange = it }
            )

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    selectedUniversity = Constants.UNIVERSITY_ALL
                    selectedGender = Constants.GENDER_ANY
                    ageRange = Constants.AGE_MIN_DEFAULT.toFloat()..Constants.AGE_MAX_DEFAULT.toFloat()
                }) {
                    Text("Сбросить")
                }

                Button(onClick = {
                    onApplyFilters(
                        selectedUniversity,
                        selectedGender,
                        ageRange.start.toInt(),
                        ageRange.endInclusive.toInt()
                    )
                }) {
                    Text("Применить")
                }
            }
        }
    }
}

@Composable
fun DropdownMenuForUniversity(
    selectedUniversity: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedUniversity)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Constants.UNIVERSITIES_LIST.forEach { university ->
                DropdownMenuItem(
                    text = { Text(university) },
                    onClick = {
                        onSelect(university)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GenderRadioGroup(
    selectedGender: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Constants.GENDERS_LIST.forEach { gender ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedGender == gender,
                    onClick = { onSelect(gender) }
                )
                Spacer(Modifier.width(8.dp))
                Text(gender, color = Color.Black)
            }
        }
    }
}

@Composable
fun AgeRangeSlider(
    ageRange: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    RangeSlider(
        value = ageRange,
        onValueChange = { newRange ->
            val clampedStart = newRange.start.coerceIn(
                Constants.AGE_MIN_DEFAULT.toFloat(),
                Constants.AGE_MAX_DEFAULT.toFloat()
            )
            val clampedEnd = newRange.endInclusive.coerceIn(
                Constants.AGE_MIN_DEFAULT.toFloat(),
                Constants.AGE_MAX_DEFAULT.toFloat()
            )
            onRangeChange(clampedStart..clampedEnd)
        },
        valueRange = Constants.AGE_MIN_DEFAULT.toFloat()..Constants.AGE_MAX_DEFAULT.toFloat(),
        steps = (Constants.AGE_MAX_DEFAULT - Constants.AGE_MIN_DEFAULT)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewGenderRadioGroup() {
    GenderRadioGroup(
        selectedGender = Constants.GENDER_ANY,
        onSelect = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAgeRangeSlider() {
    AgeRangeSlider(
        ageRange = Constants.AGE_MIN_DEFAULT.toFloat()..Constants.AGE_MAX_DEFAULT.toFloat(),
        onRangeChange = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewFiltersScreen() {
    val fakeState = MainScreenState()
    FiltersScreen(
        state = fakeState,
        onClose = {},
        onApplyFilters = { _, _, _, _ -> }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMainScreen() {
    MainScreenContent(
        profiles = emptyList(),
        currentIndex = -1,
        isLoading = false,
        error = null,
        showAllViewed = false,
        swipeRight = {},
        swipeLeft = {},
        openProfileDetails = {},
        onGoChats= {  },
        onGoProfile = {  },
        retry = {},
        selectedUniversityFilter = Constants.UNIVERSITY_ALL,
        onOpenFilters = {}
    )
}