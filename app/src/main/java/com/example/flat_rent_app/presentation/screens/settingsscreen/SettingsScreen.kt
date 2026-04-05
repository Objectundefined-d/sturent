package com.example.flat_rent_app.presentation.screens.settingsscreen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flat_rent_app.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import com.example.flat_rent_app.presentation.theme.LocalThemeController
import com.example.flat_rent_app.presentation.viewmodel.settingsviewmodel.SettingsUiState
import com.example.flat_rent_app.presentation.viewmodel.settingsviewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onDeleteAccount: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val themeController = LocalThemeController.current

    SettingsScreenContent(
        state = state,
        onBack = onBack,
        onNotifyMatchesChange = viewModel::setNotifyMatches,
        onNotifyMessagesChange = viewModel::setNotifyMessages,
        onRetry = viewModel::loadSettings,
        onThemeChange = { value ->
            viewModel.setNewTheme(value)
            themeController.setDark(value)
        },
        onDeleteAccount = onDeleteAccount
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    state: SettingsUiState,
    onBack: () -> Unit,
    onNotifyMatchesChange: (Boolean) -> Unit,
    onNotifyMessagesChange: (Boolean) -> Unit,
    onThemeChange: (Boolean) -> Unit,
    onRetry: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить аккаунт?") },
            text = { Text("Это действие нельзя отменить. Все данные будут удалены.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = onRetry) {
                            Text(text = stringResource(R.string.repeat))
                        }
                    }
                }

                else -> {
                    Column {
                        Text(
                            text = stringResource(R.string.notifications),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        ListItem(
                            headlineContent = { Text(text = stringResource(R.string.matches_notifications)) },
                            trailingContent = {
                                Switch(
                                    checked = state.notifyMatches,
                                    onCheckedChange = onNotifyMatchesChange
                                )
                            }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text(text = stringResource(R.string.messages_notifications)) },
                            trailingContent = {
                                Switch(
                                    checked = state.notifyMessages,
                                    onCheckedChange = onNotifyMessagesChange
                                )
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = stringResource(R.string.external_view),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        ListItem(
                            headlineContent = { Text(text = stringResource(R.string.dark_theme)) },
                            trailingContent = {
                                Switch(
                                    checked = state.isDarkTheme,
                                    onCheckedChange = onThemeChange
                                )
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(
                            text = "Аккаунт",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "Удалить аккаунт",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            modifier = Modifier.clickable { showDeleteDialog = true }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsScreenPreviewLight() {
    FlatrentappTheme {
        SettingsScreenContent(
            state = SettingsUiState(
                notifyMatches = true,
                notifyMessages = true,
                isDarkTheme = false
            ),
            onBack = {},
            onNotifyMatchesChange = {},
            onNotifyMessagesChange = {},
            onThemeChange = {},
            onRetry = {},
            onDeleteAccount = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreviewDark() {
    FlatrentappTheme {
        SettingsScreenContent(
            state = SettingsUiState(
                notifyMatches = false,
                notifyMessages = true,
                isDarkTheme = true
            ),
            onBack = {},
            onNotifyMatchesChange = {},
            onNotifyMessagesChange = {},
            onThemeChange = {},
            onRetry = {},
            onDeleteAccount = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Error")
@Composable
fun SettingsScreenPreviewError() {
    FlatrentappTheme {
        SettingsScreenContent(
            state = SettingsUiState(error = "Ошибка загрузки настроек"),
            onBack = {},
            onNotifyMatchesChange = {},
            onNotifyMessagesChange = {},
            onThemeChange = {},
            onRetry = {},
            onDeleteAccount = {}
        )
    }
}