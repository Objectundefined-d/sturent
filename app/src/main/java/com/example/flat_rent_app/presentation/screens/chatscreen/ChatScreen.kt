package com.example.flat_rent_app.presentation.screens.chatscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.flat_rent_app.domain.model.Message
import com.example.flat_rent_app.presentation.screens.chatscreen.components.Bubble
import com.example.flat_rent_app.presentation.screens.chatscreen.components.InputBar
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import com.example.flat_rent_app.presentation.viewmodel.chatviewmodel.ChatUiState
import com.example.flat_rent_app.presentation.viewmodel.chatviewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit,
    viewmodel: ChatViewModel = hiltViewModel()
) {
    val ui by viewmodel.ui.collectAsState()
    val messages by viewmodel.messages.collectAsState()
    val otherProfile by viewmodel.otherProfile.collectAsState()

    LaunchedEffect(Unit) { viewmodel.markRead() }

    ChatScreenContent(
        ui = ui,
        messages = messages,
        title = otherProfile?.name?.takeIf { it.isNotBlank() } ?: ui.otherUid,
        onBack = onBack,
        onInput = viewmodel::onInput,
        onSend = viewmodel::send,
        onDeleteMessage = { msgId, forBoth -> viewmodel.deleteMessage(msgId, forBoth) },
        onClearHistory = { forBoth -> viewmodel.clearHistory(forBoth) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(
    ui: ChatUiState,
    messages: List<Message>,
    title: String,
    onBack: () -> Unit,
    onInput: (String) -> Unit,
    onSend: () -> Unit,
    onDeleteMessage: (messageId: String, forBoth: Boolean) -> Unit,
    onClearHistory: (forBoth: Boolean) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить историю?") },
            text = { Text("Выберите способ очистки") },
            confirmButton = {
                TextButton(onClick = {
                    onClearHistory(true)
                    showClearDialog = false
                }) {
                    Text(
                        "У всех",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Отмена")
                    }
                    TextButton(onClick = {
                        onClearHistory(false)
                        showClearDialog = false
                    }) {
                        Text("Только у меня")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Очистить историю") },
                            onClick = {
                                showMenu = false
                                showClearDialog = true
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            InputBar(
                text = ui.input,
                sending = ui.sending,
                onTextChange = onInput,
                onSend = onSend
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Column(modifier = Modifier.padding(pad).fillMaxSize()) {

            ui.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { it.messageId }) { msg ->
                    var showDialog by remember { mutableStateOf(false) }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Удалить сообщение?") },
                            text = { Text("Выберите способ удаления") },
                            confirmButton = {
                                TextButton(onClick = {
                                    onDeleteMessage(msg.messageId, true)
                                    showDialog = false
                                }) {
                                    Text(
                                        "У всех",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            dismissButton = {
                                Row {
                                    TextButton(onClick = { showDialog = false }) {
                                        Text("Отмена")
                                    }
                                    TextButton(onClick = {
                                        onDeleteMessage(msg.messageId, false)
                                        showDialog = false
                                    }) {
                                        Text("Только у меня")
                                    }
                                }
                            }
                        )
                    }

                    Bubble(
                        msg = msg,
                        isMine = msg.senderUid == ui.myUid,
                        onLongClick = {
                            if (msg.senderUid == ui.myUid) showDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Light")
@Composable
fun ChatScreenPreviewLight() {
    FlatrentappTheme {
        ChatScreenContent(
            ui = ChatUiState(
                myUid = "me",
                chatId = "chat1",
                otherUid = "other",
                input = "",
                sending = false
            ),
            messages = listOf(
                Message(messageId = "1", senderUid = "me", text = "Привет!", createdAt = 0),
                Message(messageId = "2", senderUid = "other", text = "Привет, как дела?", createdAt = 1),
                Message(messageId = "3", senderUid = "me", text = "Отлично, ищу соседа", createdAt = 2),
            ),
            title = "Иван Иванов",
            onBack = {},
            onInput = {},
            onSend = {},
            onDeleteMessage = { _, _ -> },
            onClearHistory = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChatScreenPreviewDark() {
    FlatrentappTheme {
        ChatScreenContent(
            ui = ChatUiState(
                myUid = "me",
                chatId = "chat1",
                otherUid = "other",
                input = "Набираю сообщение...",
                sending = false
            ),
            messages = listOf(
                Message(messageId = "1", senderUid = "me", text = "Привет!", createdAt = 0),
                Message(messageId = "2", senderUid = "other", text = "Привет, как дела?", createdAt = 1),
            ),
            title = "Иван Иванов",
            onBack = {},
            onInput = {},
            onSend = {},
            onDeleteMessage = { _, _ -> },
            onClearHistory = {}
        )
    }
}