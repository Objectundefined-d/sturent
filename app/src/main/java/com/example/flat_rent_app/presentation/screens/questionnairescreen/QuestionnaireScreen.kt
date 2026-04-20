package com.example.flat_rent_app.presentation.screens.questionnairescreen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuestionnaireScreen(
    onBack: () -> Unit
) {
    Scaffold { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Анкета (заглушка)", style = MaterialTheme.typography.headlineMedium)
            Text("Тут позже будет заполнение анкеты + фото.")

            Button(onClick = onBack) {
                Text("Назад")
            }
        }
    }
}
