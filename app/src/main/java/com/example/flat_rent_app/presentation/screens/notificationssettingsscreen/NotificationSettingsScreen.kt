package com.example.flat_rent_app.presentation.screens.notificationssettingsscreen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("notifications_prefs", Context.MODE_PRIVATE)
    }

    var notifyMatches by remember { mutableStateOf(prefs.getBoolean("notify_matches", true)) }
    var notifyMessages by remember { mutableStateOf(prefs.getBoolean("notify_messages", true)) }

    val db = remember { com.google.firebase.firestore.FirebaseFirestore.getInstance() }
    val auth = remember { com.google.firebase.auth.FirebaseAuth.getInstance() }
    val myUid = auth.currentUser?.uid

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
                title = { Text("Управление уведомлениями") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Column(modifier = Modifier.padding(pad)) {
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
                                    .addOnSuccessListener {
                                        android.util.Log.d("NOTIFY_SETTINGS", "notifyMatches сохранён: $value")
                                    }
                                    .addOnFailureListener { e ->
                                        android.util.Log.e("NOTIFY_SETTINGS", "Ошибка: ${e.message}")
                                    }
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
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationSettingsScreenPreview() {
    MaterialTheme {
        NotificationsSettingsScreen(onBack = {})
    }
}