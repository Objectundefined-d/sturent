package com.example.flat_rent_app.presentation.screens.chatsscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.flat_rent_app.presentation.components.AppBottomBar
import com.example.flat_rent_app.presentation.screens.chatsscreen.components.ChatRow
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

    Scaffold(
        topBar = {
            if (searchVisible) {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = viewmodel::onSearchQuery,
                            placeholder = { Text("Поиск по имени") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            searchVisible = false
                            viewmodel.onSearchQuery("")
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Чаты") },
                    actions = {
                        IconButton(onClick = { searchVisible = true }) {
                            Icon(Icons.Default.Search, contentDescription = null)
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
                modifier = Modifier.padding(pad).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text(
                    if (searchQuery.isBlank()) "Пока нет чатов"
                    else "Ничего не найдено"
                )
            }
        } else {
            Column(modifier = Modifier.padding(pad).fillMaxSize()) {
                items.forEach { item ->
                    val title = item.profile?.name?.takeIf { it.isNotBlank() } ?: item.chat.otherUid
                    ChatRow(
                        chat = item.chat,
                        title = title,
                        onClick = { onOpenChat(item.chat.chatId, item.chat.otherUid) }
                    )
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
            }
        }
    }
}

