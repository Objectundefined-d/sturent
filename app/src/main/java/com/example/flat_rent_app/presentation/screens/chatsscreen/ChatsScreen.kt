package com.example.flat_rent_app.presentation.screens.chatsscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Чаты") },
                actions = {
                    IconButton(onClick = { /* TODO: поиск */ }) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            )
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
            ) { Text("Пока нет чатов") }
        } else {
            Column(modifier = Modifier.padding(pad).fillMaxSize()) {
                items.forEach { item ->
                    val title = item.profile?.name?.takeIf { it.isNotBlank() } ?: item.chat.otherUid

                    ChatRow(
                        chat = item.chat,
                        title = title,
                        onClick = { onOpenChat(item.chat.chatId, item.chat.otherUid) }
                    )
                    Divider()
                }
            }
        }
    }
}

