package com.example.flat_rent_app.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flat_rent_app.presentation.viewmodel.onboarding.OnboardingViewModel

@Composable
fun OnbNameScreen(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel
) {
    val state by viewModel.state.collectAsState()

    val canGoNext = state.name.isNotBlank() && state.city.isNotBlank() && state.eduPlace.isNotBlank()

    OnboardingScaffold(
        step = 1,
        totalSteps = 4,
        title = "Расскажите о себе",
        footer = {
            OnboardingFooter(
                onNext = {
                    if (canGoNext) onNext() else viewModel.onName(state.name)
                },
                nextEnabled = canGoNext
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OnbFieldLabel(label = "Как Вас зовут?", icon = OnbIcon.Person)
            OnbTextField(
                value = state.name,
                onValueChange = viewModel::onName,
                placeholder = "Имя",
                singleLine = true
            )

            OnbFieldLabel(label = "Город", icon = OnbIcon.Location)
            OnbTextField(
                value = state.city,
                onValueChange = viewModel::onCity,
                placeholder = "Город обучения",
                singleLine = true
            )

            OnbFieldLabel(label = "Учебное заведение", icon = OnbIcon.School)
            OnbTextField(
                value = state.eduPlace,
                onValueChange = viewModel::onEduPlace,
                placeholder = "Место учебы",
                singleLine = true,
            )

            state.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(2.dp))
        }
    }
}
