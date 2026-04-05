package com.example.flat_rent_app.presentation.screens.settingsscreen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    var showPasswordDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showVerifyDialog by remember { mutableStateOf(false) }
    var showUpdateEmailDialog by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }
    var passwordForEmail by remember { mutableStateOf("") }

    LaunchedEffect(state.passwordResetSent) {
        if (state.passwordResetSent) {
            snackbarHostState.showSnackbar("Письмо отправлено на вашу почту")
            viewModel.consumePasswordReset()
        }
    }

    LaunchedEffect(state.emailVerificationSent) {
        if (state.emailVerificationSent) {
            snackbarHostState.showSnackbar("Письмо подтверждения отправлено")
            viewModel.consumeEmailVerification()
        }
    }

    LaunchedEffect(state.emailUpdateSent) {
        if (state.emailUpdateSent) {
            snackbarHostState.showSnackbar("Письмо для подтверждения нового email отправлено")
            viewModel.consumeEmailUpdate()
            newEmail = ""
            passwordForEmail = ""
        }
    }

    LaunchedEffect(state.actionError) {
        state.actionError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeActionError()
        }
    }

    if (showVerifyDialog) {
        AlertDialog(
            onDismissRequest = { showVerifyDialog = false },
            title = { Text("Подтвердить почту?") },
            text = { Text("Письмо с ссылкой подтверждения будет отправлено на вашу почту") },
            confirmButton = {
                TextButton(onClick = {
                    showVerifyDialog = false
                    viewModel.sendEmailVerification()
                }) { Text("Отправить") }
            },
            dismissButton = {
                TextButton(onClick = { showVerifyDialog = false }) { Text("Отмена") }
            }
        )
    }

    if (showUpdateEmailDialog) {
        AlertDialog(
            onDismissRequest = {
                showUpdateEmailDialog = false
                newEmail = ""
                passwordForEmail = ""
            },
            title = { Text("Изменить почту") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Введите новый email и текущий пароль для подтверждения")
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Новый email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = passwordForEmail,
                        onValueChange = { passwordForEmail = it },
                        label = { Text("Текущий пароль") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showUpdateEmailDialog = false
                        viewModel.updateEmail(newEmail, passwordForEmail)
                    },
                    enabled = newEmail.isNotBlank() && passwordForEmail.isNotBlank()
                ) { Text("Изменить") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showUpdateEmailDialog = false
                    newEmail = ""
                    passwordForEmail = ""
                }) { Text("Отмена") }
            }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text(text = stringResource(R.string.change_password_q))},
            text = { Text(text = stringResource(R.string.blablabla))},
            confirmButton = {
                TextButton(onClick = {
                    showPasswordDialog = false
                    viewModel.sendPasswordReset()
                }) { Text(text = stringResource(R.string.send_letter)) }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

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
        onChangePassword = { showPasswordDialog = true },
        onVerifyEmail = { showVerifyDialog = true },
        onUpdateEmail = { showUpdateEmailDialog = true },
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
    onChangePassword: () -> Unit,
    onVerifyEmail: () -> Unit,
    onUpdateEmail: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.delete_acc_q)) },
            text = { Text(text = stringResource(R.string.delete_acc_attention)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(text = stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings)) },
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
                            text = stringResource(R.string.account),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        ListItem(
                            headlineContent = { Text(text = stringResource(R.string.change_password)) },
                            supportingContent = { Text(text = stringResource(R.string.blabla)) },
                            trailingContent = {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.clickable { onChangePassword() }
                        )
                        HorizontalDivider()

                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.delete_acc),
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
                        ListItem(
                            headlineContent = { Text("Подтвердить почту") },
                            supportingContent = { Text("Отправить письмо подтверждения") },
                            trailingContent = {
                                Icon(Icons.Default.ChevronRight, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            modifier = Modifier.clickable { onVerifyEmail() }
                        )
                        HorizontalDivider()
                        ListItem(
                            headlineContent = { Text("Изменить почту") },
                            supportingContent = { Text("Потребуется текущий пароль") },
                            trailingContent = {
                                Icon(Icons.Default.ChevronRight, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            modifier = Modifier.clickable { onUpdateEmail() }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true, showSystemUi = true, name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
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
            onChangePassword = {},
            onDeleteAccount = {},
            onUpdateEmail = {},
            onVerifyEmail = {}
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
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
            onChangePassword = {},
            onDeleteAccount = {},
            onUpdateEmail = {},
            onVerifyEmail = {}
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
            onChangePassword = {},
            onDeleteAccount = {},
            onUpdateEmail = {},
            onVerifyEmail = {}
        )
    }
}