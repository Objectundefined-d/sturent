package com.example.flat_rent_app.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.ui.Alignment
import com.example.flat_rent_app.domain.model.Gender
import com.example.flat_rent_app.presentation.viewmodel.onboarding.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnbNameScreen(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel
) {
    val state by viewModel.state.collectAsState()

    val canGoNext = state.name.isNotBlank()
            && state.city.isNotBlank()
            && state.eduPlace.isNotBlank()
            && state.gender != null

    OnboardingScaffold(
        step = 1,
        totalSteps = 4,
        title = "Расскажи о себе",
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

            OutlinedTextField(
                value = state.age,
                onValueChange = viewModel::onAge,
                label = { Text("Возраст") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OnbFieldLabel(label = "Пол", icon = OnbIcon.Person)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.gender == Gender.MALE,
                        onClick = { viewModel.onGender(Gender.MALE) }
                    )
                    Text("Мужской")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.gender == Gender.FEMALE,
                        onClick = { viewModel.onGender(Gender.FEMALE) }
                    )
                    Text("Женский")
                }
            }

            OnbFieldLabel(label = "Город", icon = OnbIcon.Location)
            OnbTextField(
                value = state.city,
                onValueChange = viewModel::onCity,
                placeholder = "Город обучения",
                singleLine = true
            )

            var expanded by remember { mutableStateOf(false) }
            OnbFieldLabel(label = "Учебное заведение", icon = OnbIcon.School)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = state.eduPlace,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Место учебы") },
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
