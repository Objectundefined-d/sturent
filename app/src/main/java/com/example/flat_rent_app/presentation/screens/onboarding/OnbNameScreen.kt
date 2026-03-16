package com.example.flat_rent_app.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import com.example.flat_rent_app.util.Constants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.flat_rent_app.R
import com.example.flat_rent_app.presentation.viewmodel.onboarding.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
        title = stringResource(R.string.onb_name_title),
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
            OnbFieldLabel(label = stringResource(R.string.onb_name_question), icon = OnbIcon.Person)
            OnbTextField(
                value = state.name,
                onValueChange = viewModel::onName,
                placeholder = stringResource(R.string.onb_name_placeholder),
                singleLine = true
            )

            OutlinedTextField(
                value = state.age,
                onValueChange = viewModel::onAge,
                label = { Text(stringResource(R.string.onb_age_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OnbFieldLabel(label = stringResource(R.string.onb_city_label), icon = OnbIcon.Location)
            OnbTextField(
                value = state.city,
                onValueChange = viewModel::onCity,
                placeholder = stringResource(R.string.onb_city_placeholder),
                singleLine = true
            )

            var expanded by remember { mutableStateOf(false) }
            OnbFieldLabel(label = stringResource(R.string.onb_university_label), icon = OnbIcon.School)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = state.eduPlace,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(stringResource(R.string.onb_university_placeholder)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Constants.UNIVERSITIES_FOR_PROFILE.forEach { university ->
                        DropdownMenuItem(
                            text = { Text(university) },
                            onClick = {
                                viewModel.onEduPlace(university)
                                expanded = false
                            }
                        )
                    }
                }
            }

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