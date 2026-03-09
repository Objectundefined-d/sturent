package com.example.flat_rent_app.presentation.screens.editquestionnairescreen

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flat_rent_app.domain.model.ProfilePhoto
import com.example.flat_rent_app.presentation.viewmodel.editquestionnaire.EditQuestionnaireViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditQuestionnaireScreenContent(
    name: String,
    age: String,
    city: String,
    eduPlace: String,
    description: String,
    selectedHabits: Map<String, Boolean>,
    photoSlots: List<ProfilePhoto?>,
    mainPhotoIndex: Int,
    isLoading: Boolean,
    onNameChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Моя анкета",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) { Text("Назад") }
                Button(onClick = onSave, enabled = !isLoading) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    else Text("Сохранить")
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
                    text = "Фотографии",
                    style = MaterialTheme.typography.titleMedium,
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
                                        color = if (isMain && hasPhoto) MaterialTheme.colorScheme.primary else Color.Transparent,
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
                                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                    Icon(
                                        imageVector = if (isMain) Icons.Default.Star else Icons.Outlined.StarOutline,
                                        contentDescription = null,
                                        tint = if (isMain) Color(0xFFFFD700) else Color.White,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(6.dp)
                                            .size(20.dp)
                                            .clickable { if (!isMain) onSetMainPhoto(index) }
                                    )
                                } else {
                                    Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                                }
                            }

                            Text(
                                text = if (isMain && hasPhoto) "★ Главное" else if (hasPhoto) "Нажми ★" else "Добавить",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = if (isMain && hasPhoto) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.length <= 3) onAgeChanged(it) },
                    label = { Text("Возраст") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = onCityChanged,
                    label = { Text("Город") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = eduPlace,
                    onValueChange = onEduPlaceChanged,
                    label = { Text("Учебное заведение") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChanged,
                    label = { Text("О себе") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Text(
                    text = "Привычки и предпочтения",
                    style = MaterialTheme.typography.titleMedium,
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
    viewModel: EditQuestionnaireViewModel
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
        city = state.city,
        eduPlace = state.eduPlace,
        description = state.description,
        selectedHabits = state.selectedHabits,
        photoSlots = state.photoSlots,
        mainPhotoIndex = state.mainPhotoIndex,
        isLoading = state.isLoading,
        onNameChanged = viewModel::onNameChanged,
        onAgeChanged = viewModel::onAgeChanged,
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditQuestionnaireScreenPreview() {
    MaterialTheme {
        EditQuestionnaireScreenContent(
            name = "Иван",
            age = "22",
            city = "Москва",
            eduPlace = "МГУ",
            description = "Люблю чистоту и тишину",
            selectedHabits = mapOf(
                "Курение" to false,
                "Алкоголь" to false,
                "Ночная сова" to true,
                "Ранняя пташка" to false,
                "Есть питомцы" to true,
                "Зову гостей" to false,
                "Чистюля" to true,
                "Люблю тишину" to false,
                "Люблю музыку" to false,
                "Занимаюсь спортом" to true,
            ),
            photoSlots = listOf(null, null, null),
            mainPhotoIndex = 0,
            isLoading = false,
            onNameChanged = {},
            onAgeChanged = {},
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