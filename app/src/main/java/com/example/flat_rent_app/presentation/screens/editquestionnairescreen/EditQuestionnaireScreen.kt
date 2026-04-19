package com.example.flat_rent_app.presentation.screens.editquestionnairescreen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.flat_rent_app.R
import com.example.flat_rent_app.domain.model.Gender
import com.example.flat_rent_app.domain.model.ProfilePhoto
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import com.example.flat_rent_app.presentation.viewmodel.editquestionnaire.EditQuestionnaireViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditQuestionnaireScreenContent(
    name: String,
    age: String,
    gender: Gender?,
    city: String,
    eduPlace: String,
    description: String,
    selectedHabits: Map<String, Boolean>,
    photoSlots: List<ProfilePhoto?>,
    mainPhotoIndex: Int,
    isLoading: Boolean,
    onNameChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onGenderChanged: (Gender) -> Unit,
    onCityChanged: (String) -> Unit,
    onEduPlaceChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onToggleHabit: (String) -> Unit,
    onPickPhoto: (index: Int) -> Unit,
    onDeletePhoto: (index: Int) -> Unit,
    onSetMainPhoto: (index: Int) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.my_questionnaire),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(stringResource(R.string.back))
                    }
                    Button(
                        onClick = onSave,
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.save))
                        }
                    }
                }
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.photos),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (0..2).forEach { index ->
                        val photo = photoSlots.getOrNull(index)
                        val isMain = mainPhotoIndex == index
                        val hasPhoto = photo?.fullUrl != null

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        width = if (isMain && hasPhoto) 2.dp else 0.dp,
                                        color = if (isMain && hasPhoto)
                                            MaterialTheme.colorScheme.primary
                                        else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onPickPhoto(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (hasPhoto) {
                                    AsyncImage(
                                        model = photo.fullUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { onDeletePhoto(index) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(28.dp)
                                            .background(
                                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                                CircleShape
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = stringResource(R.string.delete),
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Icon(
                                        imageVector = if (isMain) Icons.Default.Star
                                        else Icons.Outlined.StarOutline,
                                        contentDescription = stringResource(R.string.photo_set_main),
                                        tint = if (isMain) Color(0xFFFFD700)
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(6.dp)
                                            .size(20.dp)
                                            .clickable { if (!isMain) onSetMainPhoto(index) }
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = stringResource(R.string.photo_add),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Text(
                                text = when {
                                    isMain && hasPhoto -> stringResource(R.string.photo_main)
                                    hasPhoto -> stringResource(R.string.photo_click)
                                    else -> stringResource(R.string.photo_add)
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = if (isMain && hasPhoto)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(R.string.name)) },
                    placeholder = { Text(stringResource(R.string.hint_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.length <= 3) onAgeChanged(it) },
                    label = { Text(stringResource(R.string.age)) },
                    placeholder = { Text(stringResource(R.string.hint_age)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = stringResource(R.string.sex),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == Gender.MALE,
                            onClick = { onGenderChanged(Gender.MALE) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            stringResource(R.string.male),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == Gender.FEMALE,
                            onClick = { onGenderChanged(Gender.FEMALE) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            stringResource(R.string.female),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                OutlinedTextField(
                    value = city,
                    onValueChange = onCityChanged,
                    label = { Text(stringResource(R.string.city)) },
                    placeholder = { Text(stringResource(R.string.hint_city)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = eduPlace,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(stringResource(R.string.educational_institution)) },
                        placeholder = { Text(stringResource(R.string.hint_university)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Constants.UNIVERSITIES_FOR_PROFILE.forEach { university ->
                            DropdownMenuItem(
                                text = { Text(university) },
                                onClick = {
                                    onEduPlaceChanged(university)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChanged,
                    label = { Text(stringResource(R.string.about_me)) },
                    placeholder = { Text(stringResource(R.string.hint_about)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = stringResource(R.string.habits_and_preferences),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Column {
                    selectedHabits.keys.toList().chunked(2).forEach { rowHabits ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowHabits.forEach { habit ->
                                val isSelected = selectedHabits[habit] ?: false
                                Box(modifier = Modifier.weight(1f)) {
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onToggleHabit(habit) },
                                        label = {
                                            Text(
                                                text = habit,
                                                maxLines = 2,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            if (rowHabits.size == 1) Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditQuestionnaireScreen(
    onBack: () -> Unit,
    onSaveComplete: () -> Unit,
    viewModel: EditQuestionnaireViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var activeSlot by remember { mutableIntStateOf(0) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadPhoto(context, activeSlot, it) }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onSaveComplete()
    }

    EditQuestionnaireScreenContent(
        name = state.name,
        age = state.age,
        gender = state.gender,
        city = state.city,
        eduPlace = state.eduPlace,
        description = state.description,
        selectedHabits = state.selectedHabits,
        photoSlots = state.photoSlots,
        mainPhotoIndex = state.mainPhotoIndex,
        isLoading = state.isLoading,
        onNameChanged = viewModel::onNameChanged,
        onAgeChanged = viewModel::onAgeChanged,
        onGenderChanged = viewModel::onGenderChanged,
        onCityChanged = viewModel::onCityChanged,
        onEduPlaceChanged = viewModel::onEduPlaceChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onToggleHabit = viewModel::toggleHabit,
        onPickPhoto = { index ->
            activeSlot = index
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onDeletePhoto = viewModel::deletePhoto,
        onSetMainPhoto = viewModel::setMainPhoto,
        onSave = viewModel::saveProfile,
        onBack = onBack
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Light")
@Composable
fun EditQuestionnaireScreenPreviewLight() {
    FlatrentappTheme {
        EditQuestionnaireScreenContent(
            name = "Иван",
            age = "22",
            gender = Gender.MALE,
            city = "Москва",
            eduPlace = "МГУ",
            description = "Ищу тихого соседа",
            selectedHabits = mapOf(
                "Курение" to false,
                "Алкоголь" to false,
                "Сова" to true,
                "Жаворонок" to false,
                "Животные" to true,
                "Гости" to false,
                "Чистота" to true,
                "Тишина" to false,
            ),
            photoSlots = listOf(null, null, null),
            mainPhotoIndex = 0,
            isLoading = false,
            onNameChanged = {},
            onAgeChanged = {},
            onGenderChanged = {},
            onCityChanged = {},
            onEduPlaceChanged = {},
            onDescriptionChanged = {},
            onToggleHabit = {},
            onPickPhoto = {},
            onDeletePhoto = {},
            onSetMainPhoto = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EditQuestionnaireScreenPreviewDark() {
    FlatrentappTheme {
        EditQuestionnaireScreenContent(
            name = "Иван",
            age = "22",
            gender = Gender.MALE,
            city = "Москва",
            eduPlace = "МГТУ им. Н.Э. Баумана",
            description = "Ищу тихого соседа",
            selectedHabits = mapOf(
                "Курение" to false,
                "Алкоголь" to true,
                "Сова" to true,
                "Жаворонок" to false,
            ),
            photoSlots = listOf(null, null, null),
            mainPhotoIndex = 0,
            isLoading = false,
            onNameChanged = {},
            onAgeChanged = {},
            onGenderChanged = {},
            onCityChanged = {},
            onEduPlaceChanged = {},
            onDescriptionChanged = {},
            onToggleHabit = {},
            onPickPhoto = {},
            onDeletePhoto = {},
            onSetMainPhoto = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Loading")
@Composable
fun EditQuestionnaireScreenPreviewLoading() {
    FlatrentappTheme {
        EditQuestionnaireScreenContent(
            name = "Иван",
            age = "22",
            gender = Gender.MALE,
            city = "Москва",
            eduPlace = "МГУ",
            description = "Описание",
            selectedHabits = emptyMap(),
            photoSlots = listOf(null, null, null),
            mainPhotoIndex = 0,
            isLoading = true,
            onNameChanged = {},
            onAgeChanged = {},
            onGenderChanged = {},
            onCityChanged = {},
            onEduPlaceChanged = {},
            onDescriptionChanged = {},
            onToggleHabit = {},
            onPickPhoto = {},
            onDeletePhoto = {},
            onSetMainPhoto = {},
            onSave = {},
            onBack = {}
        )
    }
}