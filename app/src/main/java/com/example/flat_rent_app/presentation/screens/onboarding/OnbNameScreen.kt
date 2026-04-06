package com.example.flat_rent_app.presentation.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import com.example.flat_rent_app.R
import com.example.flat_rent_app.domain.model.Gender
import com.example.flat_rent_app.presentation.viewmodel.onboarding.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnbNameScreenContent(
    name: String,
    age: String,
    gender: Gender?,
    city: String,
    eduPlace: String,
    error: String?,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onCityChange: (String) -> Unit,
    onEduPlaceChange: (String) -> Unit,
    onNext: () -> Unit,
) {
    val canGoNext = name.isNotBlank() &&
            age.isNotBlank() &&
            city.isNotBlank() &&
            eduPlace.isNotBlank()

    OnboardingScaffold(
        step = 1,
        totalSteps = 4,
        title = stringResource(R.string.onb_name_title),
        footer = {
            OnboardingFooter(
                onNext = onNext,
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
                value = name,
                onValueChange = onNameChange,
                placeholder = stringResource(R.string.onb_name_placeholder),
                singleLine = true
            )

            var ageExpanded by remember { mutableStateOf(false) }
            OnbFieldLabel(label = "Возраст", icon = OnbIcon.Person)
            ExposedDropdownMenuBox(
                expanded = ageExpanded,
                onExpandedChange = { ageExpanded = it }
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Выберите возраст") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = ageExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = ageExpanded,
                    onDismissRequest = { ageExpanded = false }
                ) {
                    Constants.AGES_FOR_PROFILE.forEach { ageOption ->
                        DropdownMenuItem(
                            text = { Text(ageOption) },
                            onClick = {
                                onAgeChange(ageOption)
                                ageExpanded = false
                            }
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.onb_gender_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == Gender.MALE,
                        onClick = { onGenderChange(Gender.MALE) }
                    )
                    Text("Мужской")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == Gender.FEMALE,
                        onClick = { onGenderChange(Gender.FEMALE) }
                    )
                    Text("Женский")
                }
            }

            var cityExpanded by remember { mutableStateOf(false) }
            OnbFieldLabel(label = stringResource(R.string.onb_city_label), icon = OnbIcon.Location)
            ExposedDropdownMenuBox(
                expanded = cityExpanded,
                onExpandedChange = { cityExpanded = it }
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(stringResource(R.string.onb_city_placeholder)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = cityExpanded,
                    onDismissRequest = { cityExpanded = false }
                ) {
                    Constants.CITIES_FOR_PROFILE.forEach { cityOption ->
                        DropdownMenuItem(
                            text = { Text(cityOption) },
                            onClick = {
                                onCityChange(cityOption)
                                cityExpanded = false
                            }
                        )
                    }
                }
            }

            var expanded by remember { mutableStateOf(false) }
            OnbFieldLabel(label = stringResource(R.string.onb_university_label), icon = OnbIcon.School)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = eduPlace,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(stringResource(R.string.onb_university_placeholder)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Constants.UNIVERSITIES_FOR_PROFILE.forEach { university ->
                        DropdownMenuItem(
                            text = { Text(university) },
                            onClick = {
                                onEduPlaceChange(university)
                                expanded = false
                            }
                        )
                    }
                }
            }

            error?.let {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnbNameScreen(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel
) {
    val state by viewModel.state.collectAsState()

    OnbNameScreenContent(
        name = state.name,
        age = state.age,
        gender = state.gender,
        city = state.city,
        eduPlace = state.eduPlace,
        error = state.error,
        onNameChange = viewModel::onName,
        onAgeChange = viewModel::onAge,
        onGenderChange = viewModel::onGender,
        onCityChange = viewModel::onCity,
        onEduPlaceChange = viewModel::onEduPlace,
        onNext = onNext
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewOnbNameScreenEmpty() {
    OnbNameScreenContent(
        name = "",
        age = "",
        gender = null,
        city = "",
        eduPlace = "",
        error = null,
        onNameChange = {},
        onAgeChange = {},
        onGenderChange = {},
        onCityChange = {},
        onEduPlaceChange = {},
        onNext = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewOnbNameScreenFilled() {
    OnbNameScreenContent(
        name = "Иван",
        age = "22",
        gender = Gender.MALE,
        city = "Москва",
        eduPlace = "МГУ имени М. В. Ломоносова",
        error = null,
        onNameChange = {},
        onAgeChange = {},
        onGenderChange = {},
        onCityChange = {},
        onEduPlaceChange = {},
        onNext = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewOnbNameScreenError() {
    OnbNameScreenContent(
        name = "Иван",
        age = "22",
        gender = Gender.MALE,
        city = "",
        eduPlace = "",
        error = "Заполните все поля",
        onNameChange = {},
        onAgeChange = {},
        onGenderChange = {},
        onCityChange = {},
        onEduPlaceChange = {},
        onNext = {}
    )
}