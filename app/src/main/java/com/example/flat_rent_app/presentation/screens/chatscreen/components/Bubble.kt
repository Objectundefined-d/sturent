package com.example.flat_rent_app.presentation.screens.chatscreen.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flat_rent_app.domain.model.Message
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import com.example.flat_rent_app.domain.model.MessageStatus
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Bubble(
    msg: Message,
    isMine: Boolean,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = msg.text,
                    color = if (isMine) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date(msg.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMine)
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                if (isMine) {
                    val iconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    when (msg.status) {
                        MessageStatus.SENDING -> Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
                        MessageStatus.SENT -> Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
                        MessageStatus.READ -> Icon(
                            Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
                        MessageStatus.FAILED -> Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Прочитанное")
@Composable
fun BubblePreviewMineRead() {
    FlatrentappTheme {
        Bubble(
            msg = Message(text = "Привет", status = MessageStatus.READ),
            isMine = true,
            onLongClick = {}
        )
    }
}

@Preview(name = "Отправленное")
@Composable
fun BubblePreviewMineSent() {
    FlatrentappTheme {
        Bubble(
            msg = Message(text = "Привет", status = MessageStatus.SENT),
            isMine = true,
            onLongClick = {}
        )
    }
}

@Preview(name = "Отправляющееся")
@Composable
fun BubblePreviewMineSending() {
    FlatrentappTheme {
        Bubble(
            msg = Message(text = "Отправляется", status = MessageStatus.SENDING),
            isMine = true,
            onLongClick = {}
        )
    }
}

@Preview(name = "Ошибка отправки")
@Composable
fun BubblePreviewMineFailed() {
    FlatrentappTheme {
        Bubble(
            msg = Message(text = "Отправляется", status = MessageStatus.FAILED),
            isMine = true,
            onLongClick = {}
        )
    }
}

@Preview(name = "Входящее")
@Composable
fun BubblePreviewOther() {
    FlatrentappTheme {
        Bubble(
            msg = Message(text = "Привет", status = MessageStatus.READ),
            isMine = false,
            onLongClick = {}
        )
    }
}

@Preview(name = "темная тема входящее", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BubblePreviewDarkOther() {
    FlatrentappTheme {
        Bubble(
            msg = Message(text = "Привет", status = MessageStatus.READ),
            isMine = false,
            onLongClick = {}
        )
    }
}

@Preview(name = "темная тема исходящее", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BubblePreviewDarkMine() {
    FlatrentappTheme {
        Bubble(
            msg = Message(text = "Привет", status = MessageStatus.READ),
            isMine = true,
            onLongClick = {}
        )
    }
}

