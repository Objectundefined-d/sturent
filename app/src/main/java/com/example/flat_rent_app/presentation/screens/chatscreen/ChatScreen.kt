package com.example.flat_rent_app.presentation.screens.chatscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.flat_rent_app.presentation.screens.chatscreen.components.Bubble
import com.example.flat_rent_app.presentation.screens.chatscreen.components.InputBar
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
    var showMenu by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewmodel.markRead() }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить историю?") },
            text = { Text("Выберите способ очистки") },
            confirmButton = {
                TextButton(onClick = {
                    viewmodel.clearHistory(forBoth = true)
                    showClearDialog = false
                }) { Text("У всех") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { showClearDialog = false }) { Text("Отмена") }
                    TextButton(onClick = {
                        viewmodel.clearHistory(forBoth = false)
                        showClearDialog = false
                    }) { Text("Только у меня") }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(otherProfile?.name?.takeIf { it.isNotBlank() } ?: ui.otherUid) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
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
                onTextChange = viewmodel::onInput,
                onSend = viewmodel::send
            )
        }
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
                    var showMenu by remember { mutableStateOf(false) }

                    Box {
                        Bubble(msg = msg,
                            isMine = msg.senderUid == ui.myUid,
                            onLongClick = {
                                if (msg.senderUid == ui.myUid) showMenu = true
                            }
                        )

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Удалить у себя") },
                                onClick = {
                                    viewmodel.deleteMessage(msg.messageId, forBoth = false)
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Удалить у всех") },
                                onClick = {
                                    viewmodel.deleteMessage(msg.messageId, forBoth = true)
                                    showMenu = false
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}

