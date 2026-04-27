package com.example.flat_rent_app.presentation.screens.chatsscreen.components

import com.example.flat_rent_app.presentation.theme.Dimens

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.flat_rent_app.domain.model.Chat
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatRow(
    chat: Chat,
    title: String,
    photoUrl: String?,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = Dimens.dp12, vertical = Dimens.dp10),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            model = photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(Dimens.dp44)
                .clip(CircleShape),
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            },
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        )

        Spacer(Modifier.width(Dimens.dp12))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val preview = chat.lastMessageText.orEmpty()
            if (preview.isNotBlank()) {
                Text(
                    text = preview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (chat.unreadCount > 0) {
            Spacer(Modifier.width(Dimens.dp10))
            Badge(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(chat.unreadCount.toString())
            }
        }
    }
}

@Preview(showBackground = true, name = "Light — без фото")
@Composable
fun ChatRowPreviewNoPhoto() {
    FlatrentappTheme {
        ChatRow(
            chat = Chat(
                chatId = "1",
                otherUid = "uid1",
                lastMessageText = "Привет, как дела?",
                unreadCount = 3L
            ),
            title = "Иван Иванов",
            photoUrl = null,
            onClick = {},
            onLongClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Light — без бейджа")
@Composable
fun ChatRowPreviewNoBadge() {
    FlatrentappTheme {
        ChatRow(
            chat = Chat(
                chatId = "2",
                otherUid = "uid2",
                lastMessageText = "Ищу соседа с сентября",
                unreadCount = 0L
            ),
            title = "Мария Петрова",
            photoUrl = null,
            onClick = {},
            onLongClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Light — новый чат")
@Composable
fun ChatRowPreviewEmpty() {
    FlatrentappTheme {
        ChatRow(
            chat = Chat(
                chatId = "3",
                otherUid = "uid3",
                lastMessageText = null,
                unreadCount = 0L
            ),
            title = "Алексей Смирнов",
            photoUrl = null,
            onClick = {},
            onLongClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChatRowPreviewDark() {
    FlatrentappTheme {
        ChatRow(
            chat = Chat(
                chatId = "4",
                otherUid = "uid4",
                lastMessageText = "До встречи!",
                unreadCount = 1L
            ),
            title = "Дмитрий Козлов",
            photoUrl = null,
            onClick = {},
            onLongClick = {}
        )
    }
}
