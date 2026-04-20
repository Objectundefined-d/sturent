package com.example.flat_rent_app.presentation.screens.settingsscreen

import com.example.flat_rent_app.presentation.theme.Dimens

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
import com.example.flat_rent_app.presentation.screens.blacklistscreen.BlackListScreen
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

    val messageOne = stringResource(R.string.letter_sended_to_post)
    LaunchedEffect(state.passwordResetSent) {
        if (state.passwordResetSent) {
            snackbarHostState.showSnackbar(messageOne)
            viewModel.consumePasswordReset()
        }
    }

    val messageTwo = stringResource(R.string.confirm_message_sent)
    LaunchedEffect(state.emailVerificationSent) {
        if (state.emailVerificationSent) {
            snackbarHostState.showSnackbar(messageTwo)
            viewModel.consumeEmailVerification()
        }
    }

    val messageThree = stringResource(R.string.confirm_message_sent)
    LaunchedEffect(state.emailUpdateSent) {
        if (state.emailUpdateSent) {
            snackbarHostState.showSnackbar(messageThree)
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
            title = { Text(stringResource(R.string.confirm_email_q)) },
            text = { Text(stringResource(R.string.confirmation_email_will_be_sent_to_your_email_address)) },
            confirmButton = {
                TextButton(onClick = {
                    showVerifyDialog = false
                    viewModel.sendEmailVerification()
                }) { Text(stringResource(R.string.send)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showVerifyDialog = false
                }) { Text(stringResource(R.string.cancellation)) }
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
            title = { Text(stringResource(R.string.change_email)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.dp8)) {
                    Text(stringResource(R.string.enter_new_email_and_current_password_confirm))
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text(stringResource(R.string.new_email)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = passwordForEmail,
                        onValueChange = { passwordForEmail = it },
                        label = { Text(stringResource(R.string.new_password)) },
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
                ) { Text(stringResource(R.string.change)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showUpdateEmailDialog = false
                    newEmail = ""
                    passwordForEmail = ""
                }) { Text(stringResource(R.string.cancellation)) }
            }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text(text = stringResource(R.string.change_password_q)) },
            text = { Text(text = stringResource(R.string.blablabla)) },
            confirmButton = {
                TextButton(onClick = {
                    showPasswordDialog = false
                    viewModel.sendPasswordReset()
                }) { Text(text = stringResource(R.string.send_letter)) }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text(stringResource(R.string.cancellation))
                }
            }
        )
    }

    if (state.showBlackList) {
        BlackListScreen(
            onBack = onBack
        )
    } else {
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
            onDeleteAccount = onDeleteAccount,
            onOpenBlackList = viewModel::openBlackList
        )
    }
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
    onOpenBlackList: () -> Unit
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
                        verticalArrangement = Arrangement.spacedBy(Dimens.dp12)
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
                            modifier = Modifier.padding(horizontal = Dimens.dp16, vertical = Dimens.dp8)
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

                        HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.dp8))

                        Text(
                            text = stringResource(R.string.external_view),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = Dimens.dp16, vertical = Dimens.dp8)
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
                        HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.dp8))

                        Text(
                            text = stringResource(R.string.account),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = Dimens.dp16, vertical = Dimens.dp8)
                        )

                        ListItem(
                            headlineContent = { Text(text = stringResource(R.string.black_list)) },
                            trailingContent = {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.clickable { onOpenBlackList() }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.dp8))


                        Text(
                            text = stringResource(R.string.actions),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = Dimens.dp16, vertical = Dimens.dp8)
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
                            headlineContent = { Text(stringResource(R.string.confirm_email)) },
                            supportingContent = { Text(stringResource(R.string.send_confirm_message)) },
                            trailingContent = {
                                Icon(
                                    Icons.Default.ChevronRight, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.clickable { onVerifyEmail() }
                        )

                        HorizontalDivider()

                        ListItem(
                            headlineContent = { Text(stringResource(R.string.change_email)) },
                            supportingContent = { Text(stringResource(R.string.need_current_password)) },
                            trailingContent = {
                                Icon(
                                    Icons.Default.ChevronRight, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.clickable { onUpdateEmail() }
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
            onBack = { },
            onNotifyMatchesChange = { },
            onNotifyMessagesChange = { },
            onThemeChange = { },
            onRetry = { },
            onChangePassword = { },
            onDeleteAccount = { },
            onUpdateEmail = { },
            onVerifyEmail = { },
            onOpenBlackList = { }
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
            onBack = { },
            onNotifyMatchesChange = { },
            onNotifyMessagesChange = { },
            onThemeChange = { },
            onRetry = { },
            onChangePassword = { },
            onDeleteAccount = { },
            onUpdateEmail = { },
            onVerifyEmail = { },
            onOpenBlackList = { }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Error")
@Composable
fun SettingsScreenPreviewError() {
    FlatrentappTheme {
        SettingsScreenContent(
            state = SettingsUiState(error = "Ошибка загрузки настроек"),
            onBack = { },
            onNotifyMatchesChange = { },
            onNotifyMessagesChange = { },
            onThemeChange = { },
            onRetry = { },
            onChangePassword = { },
            onDeleteAccount = { },
            onUpdateEmail = { },
            onVerifyEmail = { },
            onOpenBlackList = { }
        )
    }
}
