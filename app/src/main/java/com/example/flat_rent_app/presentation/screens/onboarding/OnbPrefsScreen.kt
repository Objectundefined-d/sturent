package com.example.flat_rent_app.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun OnbPrefsScreen(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val options = listOf(
        Constants.SMOKING_ALLOWED,
        Constants.DRINKS_ALCOHOL,
        Constants.NIGHT_OWL,
        Constants.EARLY_BIRD,
        Constants.HAS_PETS,
        Constants.INVITES_GUESTS,
        Constants.VALUES_CLEANLINESS,
        Constants.VALUES_QUIET,
        Constants.LOVES_MUSIC,
        Constants.DOES_SPORTS,
    )

    OnboardingScaffold(
        step = 3,
        totalSteps = 4,
        title = "Ваши предпочтения",
        footer = {
            OnboardingFooter(
                onBack = onBack,
                onNext = onNext,
                nextEnabled = true
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ChipFlowRow(
                items = options,
                selected = state.preferences,
                onToggle = viewModel::togglePreference
            )

            state.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}