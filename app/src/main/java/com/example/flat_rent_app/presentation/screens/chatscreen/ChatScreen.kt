package com.example.flat_rent_app.presentation.screens.chatscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.flat_rent_app.R
import com.example.flat_rent_app.domain.model.Message
import com.example.flat_rent_app.presentation.screens.chatscreen.components.Bubble
import com.example.flat_rent_app.presentation.screens.chatscreen.components.InputBar
import com.example.flat_rent_app.presentation.screens.profiledetailscreen.ProfileDetailScreen
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import com.example.flat_rent_app.presentation.viewmodel.chatviewmodel.ChatUiState
import com.example.flat_rent_app.presentation.viewmodel.chatviewmodel.ChatViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit,
    viewmodel: ChatViewModel = hiltViewModel()
) {
    val state by viewmodel.state.collectAsState()
    val messages by viewmodel.messages.collectAsState()
    val otherProfile by viewmodel.otherProfile.collectAsState()

    LaunchedEffect(Unit) { viewmodel.markRead() }

    if (state.showProfileDetails) {
        val profile = otherProfile?.toSwipeProfile()
        if (profile != null) {
            ProfileDetailScreen(
                profile = profile,
                onBack = viewmodel::closeProfileDetails
            )
            return
        }
    }

    ChatScreenContent(
        state = state,
        messages = messages,
        title = otherProfile?.name?.takeIf { it.isNotBlank() } ?: state.otherUid,
        onBack = onBack,
        onInput = viewmodel::onInput,
        onSend = viewmodel::send,
        onDeleteMessage = { msgId, forBoth -> viewmodel.deleteMessage(msgId, forBoth) },
        onClearHistory = { forBoth -> viewmodel.clearHistory(forBoth) },
        onEditMessage = { msgId, text -> viewmodel.editMessage(msgId, text) },
        onOpenProfileDetails = viewmodel::openProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(
    state: ChatUiState,
    messages: List<Message>,
    title: String,
    onBack: () -> Unit,
    onInput: (String) -> Unit,
    onSend: () -> Unit,
    onDeleteMessage: (messageId: String, forBoth: Boolean) -> Unit,
    onClearHistory: (forBoth: Boolean) -> Unit,
    onEditMessage: (messageId: String, newText: String) -> Unit,
    onOpenProfileDetails: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var selectedMessage by remember { mutableStateOf<Message?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showReadTimeDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    var showDateOverlay by remember { mutableStateOf(false) }
    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

    LaunchedEffect(isScrolling) {
        if (isScrolling) showDateOverlay = true
        else { delay(1000); showDateOverlay = false }
    }

    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val currentDateHeader = remember(firstVisibleIndex, messages) {
        messages.getOrNull(firstVisibleIndex)?.createdAt?.let { formatDateHeader(it) } ?: ""
    }

    if (showEditDialog && selectedMessage != null) {
        var editText by remember { mutableStateOf(selectedMessage!!.text) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Редактировать сообщение") },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onEditMessage(selectedMessage!!.messageId, editText)
                    showEditDialog = false
                    selectedMessage = null
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Отмена") }
            }
        )
    }

    if (showReadTimeDialog && selectedMessage != null) {
        AlertDialog(
            onDismissRequest = { showReadTimeDialog = false },
            title = { Text("Информация о сообщении") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Отправлено: ${SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
                        .format(Date(selectedMessage!!.createdAt))}")
                    selectedMessage!!.readAt?.let {
                        Text("Прочитано: ${SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
                            .format(Date(it))}")
                    } ?: Text("Не прочитано")
                }
            },
            confirmButton = {
                TextButton(onClick = { showReadTimeDialog = false }) { Text("OK") }
            }
        )
    }

    if (selectedMessage != null && !showEditDialog && !showReadTimeDialog) {
        val clipboardManager = LocalClipboardManager.current
        ModalBottomSheet(
            onDismissRequest = { selectedMessage = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                ListItem(
                    headlineContent = { Text("Копировать") },
                    leadingContent = { Icon(Icons.Default.ContentCopy, null) },
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString(selectedMessage!!.text))
                        selectedMessage = null
                    }
                )
                if (selectedMessage!!.senderUid == state.myUid) {
                    ListItem(
                        headlineContent = { Text("Редактировать") },
                        leadingContent = { Icon(Icons.Default.Edit, null) },
                        modifier = Modifier.clickable { showEditDialog = true }
                    )
                    ListItem(
                        headlineContent = { Text("Время прочтения") },
                        leadingContent = { Icon(Icons.Default.DoneAll, null) },
                        modifier = Modifier.clickable { showReadTimeDialog = true }
                    )
                    ListItem(
                        headlineContent = { Text("Удалить у себя") },
                        leadingContent = {
                            Icon(Icons.Default.Delete, null,
                                tint = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.clickable {
                            onDeleteMessage(selectedMessage!!.messageId, false)
                            selectedMessage = null
                        }
                    )
                    ListItem(
                        headlineContent = {
                            Text("Удалить у всех", color = MaterialTheme.colorScheme.error)
                        },
                        leadingContent = {
                            Icon(Icons.Default.Delete, null,
                                tint = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.clickable {
                            onDeleteMessage(selectedMessage!!.messageId, true)
                            selectedMessage = null
                        }
                    )
                } else {
                    ListItem(
                        headlineContent = { Text("Удалить у себя") },
                        leadingContent = {
                            Icon(Icons.Default.Delete, null,
                                tint = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.clickable {
                            onDeleteMessage(selectedMessage!!.messageId, false)
                            selectedMessage = null
                        }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.delete_history)) },
            text = { Text("Выберите способ очистки") },
            confirmButton = {
                TextButton(onClick = {
                    onClearHistory(true)
                    showClearDialog = false
                }) { Text("У всех", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { showClearDialog = false }) { Text("Отмена") }
                    TextButton(onClick = {
                        onClearHistory(false)
                        showClearDialog = false
                    }) { Text("Только у меня") }
                }
            }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { TextButton(onClick = onOpenProfileDetails ) { Text(text = title) } },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
                error = state.error,
                text = state.input,
                sending = state.sending,
                onTextChange = onInput,
                onSend = onSend
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Box(modifier = Modifier
            .padding(pad)
            .fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages, key = { it.messageId }) { msg ->
                    val index = messages.indexOf(msg)
                    val needDateSeparator = index == 0 || !isSameDay(
                        Calendar.getInstance().apply { timeInMillis = messages[index - 1].createdAt },
                        Calendar.getInstance().apply { timeInMillis = msg.createdAt }
                    )

                    if (needDateSeparator) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ) {
                                Text(
                                    text = formatDateHeader(msg.createdAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }

                    Bubble(
                        msg = msg,
                        isMine = msg.senderUid == state.myUid,
                        onLongClick = { selectedMessage = msg }
                    )
                }
            }

            AnimatedVisibility(
                visible = showDateOverlay && currentDateHeader.isNotBlank(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = currentDateHeader,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

fun formatDateHeader(millis: Long): String {
    val msgDate = Calendar.getInstance().apply { timeInMillis = millis }
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        isSameDay(msgDate, today) -> "Сегодня"
        isSameDay(msgDate, yesterday) -> "Вчера"
        else -> SimpleDateFormat("d MMMM yyyy", Locale("ru")).format(Date(millis))
    }
}

fun isSameDay(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)


@Preview(showBackground = true, showSystemUi = true, name = "Light")
@Composable
fun ChatScreenPreviewLight() {
    FlatrentappTheme {
        ChatScreenContent(
            state = ChatUiState(
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
            onClearHistory = {},
            onEditMessage = { _, _ -> },
            onOpenProfileDetails = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChatScreenPreviewDark() {
    FlatrentappTheme {
        ChatScreenContent(
            state = ChatUiState(
                myUid = "me",
                chatId = "chat1",
                otherUid = "other",
                input = "Набираю сообщение",
                sending = false,
                error = "Ошибка"
            ),
            messages = listOf(
                Message(messageId = "1", senderUid = "me", text = "Привет", createdAt = 0),
                Message(messageId = "2", senderUid = "other", text = "Привет", createdAt = 1),
            ),
            title = "Иван Иванов",
            onBack = {},
            onInput = {},
            onSend = {},
            onDeleteMessage = { _, _ -> },
            onClearHistory = {},
            onEditMessage = { _, _ -> },
            onOpenProfileDetails = {}
        )
    }
}