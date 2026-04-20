package com.example.flat_rent_app.presentation.screens.chatscreen.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import com.example.flat_rent_app.presentation.viewmodel.chatviewmodel.ChatUiState

@Composable
fun InputBar(
    error: String?,
    text: String,
    sending: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = text,
                onValueChange = onTextChange,
                placeholder = {
                    Text(
                        text = error ?: "Сообщение…",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.width(10.dp))

            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank()
            ) {
                if (sending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = null,
                        tint = if (text.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "пустое")
@Composable
fun InputBarPreviewEmpty() {
    FlatrentappTheme {
        InputBar(text = "", sending = false, onTextChange = {}, onSend = {}, error = null)
    }
}

@Preview(showBackground = true, name = "с текстом")
@Composable
fun InputBarPreviewWithText() {
    FlatrentappTheme {
        InputBar(text = "Привет", sending = false, onTextChange = {}, onSend = {}, error = null)
    }
}

@Preview(showBackground = true, name = "отправка")
@Composable
fun InputBarPreviewSending() {
    FlatrentappTheme {
        InputBar(text = "", sending = true, onTextChange = {}, onSend = {}, error = "Ошибка")
    }
}

@Preview(showBackground = true, name = "с текстом, темная тема",
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun InputBarPreviewDark() {
    FlatrentappTheme {
        InputBar(text = "Привет", sending = false, onTextChange = {}, onSend = {}, error = null)
    }
}
