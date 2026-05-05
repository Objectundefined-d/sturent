package com.example.flat_rent_app.presentation.screens.chatscreen

import com.example.flat_rent_app.presentation.theme.Dimens

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flat_rent_app.R
import com.example.flat_rent_app.domain.model.Message
import com.example.flat_rent_app.presentation.screens.chatscreen.components.Bubble
import com.example.flat_rent_app.presentation.screens.chatscreen.components.InputBar
import com.example.flat_rent_app.presentation.screens.profiledetailscreen.ProfileDetailScreen
import com.example.flat_rent_app.presentation.screens.profiledetailscreen.ProfileScreenMode
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import com.example.flat_rent_app.presentation.viewmodel.blacklistviewmodel.BlackListViewModel
import com.example.flat_rent_app.presentation.viewmodel.chatviewmodel.ChatUiState
import com.example.flat_rent_app.presentation.viewmodel.chatviewmodel.ChatViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

private const val DATE_OVERLAY_HIDE_DELAY_MS = 1000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit,
    viewmodel: ChatViewModel = hiltViewModel(),
    blacklistviewmodel: BlackListViewModel = hiltViewModel()
) {
    val state by viewmodel.state.collectAsState()
    val messages by viewmodel.messages.collectAsState()
    val otherProfile by viewmodel.otherProfile.collectAsState()

    val blackListState = blacklistviewmodel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewmodel.markRead() }

    LaunchedEffect(state.showProfileDetails) {
        if (state.showProfileDetails) {
            otherProfile?.uid?.let { blacklistviewmodel.checkIsBlocked(it) }
        }
    }

    if (state.showProfileDetails) {
        val profile = otherProfile?.toSwipeProfile()
        if (profile != null) {
            ProfileDetailScreen(
                profile = profile,
                onBack = viewmodel::closeProfileDetails,
                onAddToSkipList = { },
                onAddToBlackList = {
                    otherProfile?.uid?.let { blacklistviewmodel.blockUser(it) }
                },
                onUnblock = {
                    otherProfile?.uid?.let { blacklistviewmodel.unblockUser(it) }
                },
                isBlocked = blackListState.value.profileBlocked,
                mode = ProfileScreenMode.FROMCHAT
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
        onOpenProfileDetails = viewmodel::openProfile,
        onSearchActiveChange = viewmodel::setSearchActive,
        onSearchQueryChange = viewmodel::onSearchQueryChange,
        onClearSearch = viewmodel::clearSearch
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
    onOpenProfileDetails: () -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var selectedMessage by remember { mutableStateOf<Message?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showReadTimeDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }
    var showDateOverlay by remember { mutableStateOf(false) }
    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

    var scrollToMessageId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isScrolling) {
        if (isScrolling) showDateOverlay = true
        else { delay(DATE_OVERLAY_HIDE_DELAY_MS); showDateOverlay = false }
    }

    val filteredMessages = remember(messages, state.searchQuery) {
        if (state.searchQuery.isBlank()) messages
        else messages.filter { it.text.contains(state.searchQuery, ignoreCase = true) }
    }

    LaunchedEffect(scrollToMessageId) {
        scrollToMessageId?.let { id ->
            val index = messages.indexOfFirst { it.messageId == id }
            if (index >= 0) {
                listState.animateScrollToItem(index)
            }
            scrollToMessageId = null
        }
    }

    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val currentDateHeader = remember(firstVisibleIndex, messages) {
        messages.getOrNull(firstVisibleIndex)?.createdAt?.let { formatDateHeader(it) } ?: ""
    }

    val scope = rememberCoroutineScope()

    val showScrollToBottom = remember(messages.size) {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            messages.isNotEmpty() && lastVisibleIndex < messages.size - 1
        }
    }.value


    if (showEditDialog && selectedMessage != null) {
        var editText by remember { mutableStateOf(selectedMessage!!.text) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.edit_the_message)) },
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
                }) { Text(stringResource(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text(text = stringResource(R.string.cancel)) }
            }
        )
    }

    if (showReadTimeDialog && selectedMessage != null) {
        val message = selectedMessage ?: return
        val sentDateString = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
            .format(Date(message.createdAt))
        val readDateString = message.readAt?.let {
            SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(Date(it))
        }
        AlertDialog(
            onDismissRequest = { showReadTimeDialog = false },
            title = { Text(stringResource(R.string.information_about_message)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.dp4)) {
                    Text(stringResource(R.string.sent_at, sentDateString))
                    if (readDateString != null) {
                        Text(stringResource(R.string.read_at, readDateString))
                    } else {
                        Text(stringResource(R.string.not_readed))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReadTimeDialog = false }) {
                    Text(stringResource(R.string.ok))
                }
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
                    .padding(bottom = Dimens.dp32)
            ) {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.copy)) },
                    leadingContent = { Icon(Icons.Default.ContentCopy, null) },
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString(selectedMessage!!.text))
                        selectedMessage = null
                    }
                )
                if (selectedMessage!!.senderUid == state.myUid) {
                    ListItem(
                        headlineContent = { Text(text = stringResource(R.string.edit)) },
                        leadingContent = { Icon(Icons.Default.Edit, null) },
                        modifier = Modifier.clickable { showEditDialog = true }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.read_time)) },
                        leadingContent = { Icon(Icons.Default.DoneAll, null) },
                        modifier = Modifier.clickable { showReadTimeDialog = true }
                    )
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.delete_for_you)) },
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
                            Text(stringResource(R.string.delete_for_both), color = MaterialTheme.colorScheme.error)
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
                        headlineContent = { Text(stringResource(R.string.delete_for_you)) },
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
            text = { Text(stringResource(R.string.choose_cleaning_method)) },
            confirmButton = {
                TextButton(onClick = {
                    onClearHistory(true)
                    showClearDialog = false
                }) { Text(stringResource(R.string.for_both), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.cancel)) }
                    TextButton(onClick = {
                        onClearHistory(false)
                        showClearDialog = false
                    }) { Text(stringResource(R.string.only_for_me)) }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            if (state.isSearchActive) {
                TopAppBar(
                    title = {
                        TextField(
                            value = state.searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text(stringResource(R.string.search_messages)) },
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
                        IconButton(onClick = onClearSearch) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        if (state.searchQuery.isNotBlank()) {
                            TextButton(onClick = { onSearchQueryChange("") }) {
                                Text(stringResource(R.string.clear))
                            }
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { TextButton(onClick = onOpenProfileDetails) { Text(text = title) } },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { onSearchActiveChange(true) }) {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.clean_history)) },
                                onClick = {
                                    showMenu = false
                                    showClearDialog = true
                                }
                            )
                        }
                    }
                )
            }
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
                    .padding(horizontal = Dimens.dp12),
                verticalArrangement = Arrangement.spacedBy(Dimens.dp8),
                contentPadding = PaddingValues(vertical = Dimens.dp12)
            ) {
                items(filteredMessages, key = { it.messageId }) { msg ->
                    val index = filteredMessages.indexOf(msg)
                    if (!state.isSearchActive) {
                        val needDateSeparator = if (index == 0) true else {
                            val prevMsg = filteredMessages[index - 1]
                            !isSameDay(
                                Calendar.getInstance().apply { timeInMillis = prevMsg.createdAt },
                                Calendar.getInstance().apply { timeInMillis = msg.createdAt }
                            )
                        }
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
                    }

                    Bubble(
                        msg = msg,
                        isMine = msg.senderUid == state.myUid,
                        onLongClick = { selectedMessage = msg },
                        onClick = {
                            if (state.isSearchActive) {
                                run {
                                    scrollToMessageId = msg.messageId
                                    onClearSearch()
                                }
                            } else null
                        },
                        highlightText = if (state.isSearchActive) state.searchQuery else null
                    )
                }
            }

            if (!state.isSearchActive) {
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

            AnimatedVisibility(
                visible = showScrollToBottom,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
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
                sending = false,
                isSearchActive = false,
                searchQuery = ""
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
            onOpenProfileDetails = {},
            onSearchActiveChange = {},
            onSearchQueryChange = {},
            onClearSearch = {}
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
                error = "Ошибка",
                isSearchActive = false,
                searchQuery = ""
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
            onOpenProfileDetails = {},
            onSearchActiveChange = {},
            onSearchQueryChange = {},
            onClearSearch = {}
        )
    }
}
