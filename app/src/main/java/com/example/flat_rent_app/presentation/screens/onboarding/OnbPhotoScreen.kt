package com.example.flat_rent_app.presentation.screens.onboarding

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flat_rent_app.domain.model.ProfilePhoto
import com.example.flat_rent_app.presentation.viewmodel.onboarding.OnboardingViewModel
import androidx.compose.runtime.setValue

@Composable
fun OnbPhotoScreenContent(
    uploadedPhotos: List<ProfilePhoto?>,
    pickedUris: List<Uri?>,
    mainPhotoIndex: Int,
    loading: Boolean,
    error: String?,
    onPickPhoto: (index: Int) -> Unit,
    onUpload: (index: Int) -> Unit,
    onSetMain: (index: Int) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val canGoNext = true

    OnboardingScaffold(
        step = 2,
        totalSteps = 4,
        title = "Добавьте фото",
        footer = {
            OnboardingFooter(
                onBack = onBack,
                onNext = onNext,
                nextEnabled = canGoNext
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                (0..2).forEach { index ->
                    val model = uploadedPhotos[index]?.fullUrl ?: pickedUris[index]
                    val isMain = mainPhotoIndex == index
                    val isUploaded = uploadedPhotos[index] != null

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PhotoSlotCard(
                            modifier = Modifier
                                .height(160.dp)
                                .clickable { onPickPhoto(index) },
                            imageModel = model,
                            title = if (index == 0) "Главное" else "Фото ${index + 1}",
                            countText = ""
                        )

                        if (pickedUris[index] != null && !isUploaded) {
                            TextButton(onClick = { onUpload(index) }) {
                                Text("Загрузить", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        if (isUploaded && !isMain) {
                            TextButton(onClick = { onSetMain(index) }) {
                                Text("Главное", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        if (isMain && isUploaded) {
                            Text(
                                "Главное",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun OnbPhotoScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: OnboardingViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var activeSlot by remember { mutableIntStateOf(0) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> viewModel.onPickedPhoto(activeSlot, uri) }

    OnbPhotoScreenContent(
        uploadedPhotos = state.uploadedPhotos,
        pickedUris = state.pickedPhotoUris,
        mainPhotoIndex = state.mainPhotoIndex,
        loading = state.loading,
        error = state.error,
        onPickPhoto = { index ->
            activeSlot = index
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onUpload = { index -> viewModel.uploadPhoto(context, index) },
        onSetMain = { index -> viewModel.setMainPhoto(index) },
        onBack = onBack,
        onNext = onNext
    )
}

@Preview(showBackground = true, name = "OnbPhotoScreen - пустое состояние")
@Composable
private fun OnbPhotoScreenPreview() {
    MaterialTheme {
        OnbPhotoScreenContent(
            uploadedPhotos = listOf(null),
            pickedUris = listOf(null),
            mainPhotoIndex = 0,
            loading = false,
            error = null,
            onPickPhoto = {},
            onUpload = {},
            onSetMain = {},
            onBack = {},
            onNext = {}
        )
    }
}
