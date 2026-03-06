package com.example.flat_rent_app.presentation.screens.onboarding

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flat_rent_app.presentation.viewmodel.onboarding.OnboardingViewModel

@Composable
fun OnbPhotoScreenContent(
    uploadedPhotoUrl: String?,
    pickedPhotoUri: Uri?,
    loading: Boolean,
    error: String?,
    onPickPhoto: () -> Unit,
    onUpload: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val model = uploadedPhotoUrl ?: pickedPhotoUri
    val pickedCount = if (model != null) 1 else 0
    val canGoNext = uploadedPhotoUrl != null

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
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                PhotoSlotCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                        .clickable { onPickPhoto() },
                    imageModel = model,
                    title = "Добавить фото",
                    countText = "$pickedCount/3"
                )
            }

            PillButton(
                text = if (uploadedPhotoUrl == null) "Загрузить" else "Загружено",
                enabled = !loading && pickedPhotoUri != null,
                onClick = onUpload,
                modifier = Modifier.fillMaxWidth(),
                leading = {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(10.dp))
                    }
                }
            )

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
fun OnbPhotoScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: OnboardingViewModel
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> viewModel.onPickedPhoto(uri) }

    OnbPhotoScreenContent(
        uploadedPhotoUrl = state.uploadedPhoto?.fullUrl,
        pickedPhotoUri = state.pickedPhotoUri,
        loading = state.loading,
        error = state.error,
        onPickPhoto = {
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onUpload = { viewModel.uploadMainPhoto(context) },
        onBack = onBack,
        onNext = onNext
    )
}

@Preview(showBackground = true, name = "OnbPhotoScreen - пустое состояние")
@Composable
private fun OnbPhotoScreenPreview() {
    MaterialTheme {
        OnbPhotoScreenContent(
            uploadedPhotoUrl = null,
            pickedPhotoUri = null,
            loading = false,
            error = null,
            onPickPhoto = {},
            onUpload = {},
            onBack = {},
            onNext = {}
        )
    }
}
