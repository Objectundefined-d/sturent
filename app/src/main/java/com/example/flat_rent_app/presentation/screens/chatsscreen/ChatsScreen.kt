package com.example.flat_rent_app.presentation.screens.chatsscreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.flat_rent_app.R
import com.example.flat_rent_app.domain.model.Chat
import com.example.flat_rent_app.domain.model.ChatUiItem
import com.example.flat_rent_app.presentation.components.AppBottomBar
import com.example.flat_rent_app.presentation.screens.chatsscreen.components.ChatRow
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import com.example.flat_rent_app.presentation.viewmodel.chatsviewmodel.ChatsViewModel
import com.example.flat_rent_app.util.BottomTabs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    onOpenChat: (chatId: String, otherUid: String) -> Unit,
    onGoHome: () -> Unit,
    onGoProfile: () -> Unit,
    onGoFavorites: () -> Unit,
    viewmodel: ChatsViewModel = hiltViewModel()
) {
    val items by viewmodel.items.collectAsState()
    val searchQuery by viewmodel.searchQuery.collectAsState()
    var searchVisible by remember { mutableStateOf(false) }
    var chatToDelete by remember { mutableStateOf<ChatUiItem?>(null) }

    ChatsScreenContent(
        searchVisible = searchVisible,
        onSearchVisibleChange = { searchVisible = it },
        items = items,
        searchQuery = searchQuery,
        onSearchQuery = viewmodel::onSearchQuery,
        onOpenChat = onOpenChat,
        onGoHome = onGoHome,
        onGoProfile = onGoProfile,
        onGoFavorites = onGoFavorites,
        chatToDelete = chatToDelete,
        onDeleteChat = { item, forBoth ->
            viewmodel.deleteChat(item.chat.chatId, item.chat.otherUid, forBoth)
            chatToDelete = null
        },
        onDismissDelete = { chatToDelete = null },
        onLongClickChat = { chatToDelete = it }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreenContent(
    searchVisible: Boolean,
    onSearchVisibleChange: (Boolean) -> Unit,
    items: List<ChatUiItem>,
    searchQuery: String,
    onSearchQuery: (String) -> Unit,
    onOpenChat: (chatId: String, otherUid: String) -> Unit,
    onGoHome: () -> Unit,
    onGoProfile: () -> Unit,
    onGoFavorites: () -> Unit,
    chatToDelete: ChatUiItem?,
    onLongClickChat: (ChatUiItem) -> Unit,
    onDeleteChat: (ChatUiItem, Boolean) -> Unit,
    onDismissDelete: () -> Unit,
) {
    chatToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = { Text(stringResource(R.string.delete_chat_q)) },
            text = { Text(stringResource(R.string.select_deletion_method)) },
            confirmButton = {
                TextButton(onClick = { onDeleteChat(item, true) }) {
                    Text(
                        stringResource(R.string.both),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = onDismissDelete) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    TextButton(onClick = { onDeleteChat(item, false) }) {
                        Text(stringResource(R.string.only_for_me))
                    }
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (searchVisible) {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = onSearchQuery,
                            placeholder = {
                                Text(
                                    stringResource(R.string.name_search),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onSearchVisibleChange(false)
                            onSearchQuery("")
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.chats),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    actions = {
                        IconButton(onClick = { onSearchVisibleChange(true) }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            AppBottomBar(
                selected = BottomTabs.CHATS,
                onHome = onGoHome,
                onChats = { },
                onProfile = onGoProfile,
                onFavorites = onGoFavorites
            )
        }
    ) { pad ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isBlank()) stringResource(R.string.there_are_no_chats_yet) else stringResource(
                        R.string.nothing_found
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(modifier = Modifier
                .padding(pad)
                .fillMaxSize()) {
                items.forEach { item ->
                    val title = item.profile?.name?.takeIf { it.isNotBlank() } ?: item.chat.otherUid
                    val photoUrl = item.profile?.photoSlots
                        ?.getOrNull(item.profile.mainPhotoIndex)?.fullUrl

                    ChatRow(
                        chat = item.chat,
                        title = title,
                        photoUrl = photoUrl,
                        onClick = { onOpenChat(item.chat.chatId, item.chat.otherUid) },
                        onLongClick = { onLongClickChat(item) }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, name = "Light — пусто")
@Composable
fun ChatsScreenPreviewEmpty() {
    FlatrentappTheme {
        ChatsScreenContent(
            searchVisible = false,
            onSearchVisibleChange = {},
            items = emptyList(),
            searchQuery = "",
            onSearchQuery = {},
            onOpenChat = { _, _ -> },
            onGoHome = {},
            onGoProfile = {},
            onGoFavorites = {},
            chatToDelete = null,
            onLongClickChat = {},
            onDeleteChat = { _, _ -> },
            onDismissDelete = {}
        )
    }
}


@Preview(showBackground = true, showSystemUi = true, name = "Light — с чатами")
@Composable
fun ChatsScreenPreviewWithChats() {
    FlatrentappTheme {
        ChatsScreenContent(
            searchVisible = false,
            onSearchVisibleChange = {},
            items = listOf(
                ChatUiItem(
                    chat = Chat(
                        chatId = "1",
                        otherUid = "uid1",
                        lastMessageText = "Привет, как дела?",
                        unreadCount = 2
                    ),
                    profile = null
                ),
                ChatUiItem(
                    chat = Chat(
                        chatId = "2",
                        otherUid = "uid2",
                        lastMessageText = "Ищу соседа с сентября",
                        unreadCount = 0
                    ),
                    profile = null
                )
            ),
            searchQuery = "",
            onSearchQuery = {},
            onOpenChat = { _, _ -> },
            onGoHome = {},
            onGoProfile = {},
            onGoFavorites = {},
            chatToDelete = null,
            onLongClickChat = {},
            onDeleteChat = { _, _ -> },
            onDismissDelete = {}
        )
    }
}


@Preview(showBackground = true, showSystemUi = true, name = "Light — поиск")
@Composable
fun ChatsScreenPreviewSearch() {
    FlatrentappTheme {
        ChatsScreenContent(
            searchVisible = true,
            onSearchVisibleChange = {},
            items = emptyList(),
            searchQuery = "Иван",
            onSearchQuery = {},
            onOpenChat = { _, _ -> },
            onGoHome = {},
            onGoProfile = {},
            onGoFavorites = {},
            chatToDelete = null,
            onLongClickChat = {},
            onDeleteChat = { _, _ -> },
            onDismissDelete = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark — с чатами",
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChatsScreenPreviewDark() {
    FlatrentappTheme {
        ChatsScreenContent(
            searchVisible = false,
            onSearchVisibleChange = {},
            items = listOf(
                ChatUiItem(
                    chat = Chat(
                        chatId = "1",
                        otherUid = "uid1",
                        lastMessageText = "Привет!",
                        unreadCount = 1
                    ),
                    profile = null
                )
            ),
            searchQuery = "",
            onSearchQuery = {},
            onOpenChat = { _, _ -> },
            onGoHome = {},
            onGoProfile = {},
            onGoFavorites = {},
            chatToDelete = null,
            onLongClickChat = {},
            onDeleteChat = { _, _ -> },
            onDismissDelete = {}
        )
    }
}