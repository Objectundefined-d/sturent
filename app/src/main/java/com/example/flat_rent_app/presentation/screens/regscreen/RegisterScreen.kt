package com.example.flat_rent_app.presentation.screens.regscreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.flat_rent_app.presentation.viewmodel.authviewmodel.AuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.flat_rent_app.R
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var passVisible by remember { mutableStateOf(false) }
    var repeatVisible by remember { mutableStateOf(false) }

    RegisterScreenContent(
        email = state.email,
        password = state.password,
        confirm = state.confirm,
        error = state.error,
        loading = state.loading,
        passVisible = passVisible,
        repeatVisible = repeatVisible,
        onBack = onBack,
        onEmailChange = viewModel::onEmail,
        onPasswordChange = viewModel::onPassword,
        onConfirmChange = viewModel::onConfirm,
        onRegisterClick = viewModel::register,
        onTogglePassVisible = { passVisible = !passVisible },
        onToggleRepeatVisible = { repeatVisible = !repeatVisible }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    email: String,
    password: String,
    confirm: String,
    error: String?,
    loading: Boolean,
    passVisible: Boolean,
    repeatVisible: Boolean,
    onBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onTogglePassVisible: () -> Unit,
    onToggleRepeatVisible: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text("") }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(14.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_account),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.reg_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(22.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = onEmailChange,
                label = { Text(stringResource(R.string.reg_email_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(28.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = onPasswordChange,
                label = { Text(stringResource(R.string.reg_password_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onTogglePassVisible) {
                        Icon(
                            imageVector = if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                shape = RoundedCornerShape(28.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = confirm,
                onValueChange = onConfirmChange,
                label = { Text(stringResource(R.string.reg_password_repeat_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (repeatVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onToggleRepeatVisible) {
                        Icon(
                            imageVector = if (repeatVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                shape = RoundedCornerShape(28.dp)
            )

            error?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(26.dp))

            Button(
                onClick = onRegisterClick,
                enabled = !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Text(stringResource(R.string.reg_submit))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun RegisterScreenPreviewLight() {
    FlatrentappTheme {
        RegisterScreenContent(
            email = "test@mail.com",
            password = "123456",
            confirm = "123456",
            error = null,
            loading = false,
            passVisible = false,
            repeatVisible = false,
            onBack = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmChange = {},
            onRegisterClick = {},
            onTogglePassVisible = {},
            onToggleRepeatVisible = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RegisterScreenPreviewDark() {
    FlatrentappTheme {
        RegisterScreenContent(
            email = "",
            password = "",
            confirm = "",
            error = "Пароли не совпадают",
            loading = false,
            passVisible = false,
            repeatVisible = false,
            onBack = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmChange = {},
            onRegisterClick = {},
            onTogglePassVisible = {},
            onToggleRepeatVisible = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Loading")
@Composable
fun RegisterScreenPreviewLoading() {
    FlatrentappTheme {
        RegisterScreenContent(
            email = "test@mail.com",
            password = "123456",
            confirm = "123456",
            error = null,
            loading = true,
            passVisible = false,
            repeatVisible = false,
            onBack = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmChange = {},
            onRegisterClick = {},
            onTogglePassVisible = {},
            onToggleRepeatVisible = {}
        )
    }
}
