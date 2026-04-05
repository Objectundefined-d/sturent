package com.example.flat_rent_app.presentation.screens.notificationssettingsscreen

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.example.flat_rent_app.presentation.theme.FlatrentappTheme
import com.example.flat_rent_app.presentation.theme.LocalThemeController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
    }

    var notifyMatches by remember { mutableStateOf(prefs.getBoolean("notify_matches", true)) }
    var notifyMessages by remember { mutableStateOf(prefs.getBoolean("notify_messages", true)) }

    val db = remember { com.google.firebase.firestore.FirebaseFirestore.getInstance() }
    val auth = remember { com.google.firebase.auth.FirebaseAuth.getInstance() }
    val myUid = auth.currentUser?.uid

    val themeController = LocalThemeController.current

    LaunchedEffect(myUid) {
        myUid ?: return@LaunchedEffect
        db.collection("users").document(myUid).get()
            .addOnSuccessListener { doc ->
                val matchesVal = doc.getBoolean("notifyMatches") ?: true
                val messagesVal = doc.getBoolean("notifyMessages") ?: true
                notifyMatches = matchesVal
                notifyMessages = messagesVal
                prefs.edit {
                    putBoolean("notify_matches", matchesVal)
                    putBoolean("notify_messages", messagesVal)
                }
            }
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
        Column(modifier = Modifier.padding(pad)) {

            // раздел уведомлений
            Text(
                text = "Уведомления",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("Уведомления о мэтчах") },
                trailingContent = {
                    Switch(
                        checked = notifyMatches,
                        onCheckedChange = { value ->
                            notifyMatches = value
                            prefs.edit { putBoolean("notify_matches", value) }
                            myUid?.let {
                                db.collection("users").document(it)
                                    .update("notifyMatches", value)
                            }
                        }
                    )
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Уведомления о сообщениях") },
                trailingContent = {
                    Switch(
                        checked = notifyMessages,
                        onCheckedChange = { value ->
                            notifyMessages = value
                            prefs.edit { putBoolean("notify_messages", value) }
                            myUid?.let {
                                db.collection("users").document(it)
                                    .update("notifyMessages", value)
                            }
                        }
                    )
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // раздел внешнего вида
            Text(
                text = "Внешний вид",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ListItem(
                headlineContent = { Text("Тёмная тема") },
                trailingContent = {
                    Switch(
                        checked = themeController.isDark.value,
                        onCheckedChange = { themeController.setDark(it) }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenPreviewContent(isDark: Boolean) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Column(modifier = Modifier.padding(pad)) {
            Text(
                text = "Уведомления",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            ListItem(
                headlineContent = { Text("Уведомления о мэтчах") },
                trailingContent = { Switch(checked = true, onCheckedChange = {}) }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Уведомления о сообщениях") },
                trailingContent = { Switch(checked = false, onCheckedChange = {}) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Внешний вид",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            ListItem(
                headlineContent = { Text("Тёмная тема") },
                trailingContent = { Switch(checked = isDark, onCheckedChange = {}) }
            )
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
        SettingsScreenPreviewContent(isDark = false)
    }
}

@Preview(
    showBackground = true, showSystemUi = true, name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SettingsScreenPreviewDark() {
    FlatrentappTheme {
        SettingsScreenPreviewContent(isDark = true)
    }
}