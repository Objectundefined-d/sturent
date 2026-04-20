package com.example.flat_rent_app.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.flat_rent_app.MainActivity
import com.example.flat_rent_app.R
import com.example.flat_rent_app.domain.repository.ProfileRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.java

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject
    lateinit var profileRepository: ProfileRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            profileRepository.saveFcmToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.data["title"] ?: return
        val body = message.data["body"] ?: return
        val type = message.data["type"] ?: "match"

        val prefs = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        val enabled = when (type) {
            "message" -> prefs.getBoolean("notifyMessages", true)
            "match" -> prefs.getBoolean("notifyMatches", true) 
            else -> true
        }
        if (!enabled) return

        val channelId = when (type) {
            "message" -> "messages_channel"
            else -> "matches_channel"
        }

        showNotification(title, body, channelId)
    }

    private fun showNotification(title: String, body: String, channelId: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(
                "matches_channel",
                "Совпадения",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
        manager.createNotificationChannel(
            NotificationChannel(
                "messages_channel",
                "Сообщения",
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
