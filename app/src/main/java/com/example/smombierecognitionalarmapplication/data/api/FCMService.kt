package com.example.smombierecognitionalarmapplication.data.api

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.smombierecognitionalarmapplication.MainActivity
import com.example.smombierecognitionalarmapplication.R
import com.example.smombierecognitionalarmapplication.data.CUSTOM_REQUEST_CODE_MAIN
import com.example.smombierecognitionalarmapplication.data.DANGERALERT_NOTIFICATION_CHANNEL_ID
import com.example.smombierecognitionalarmapplication.data.DANGERALERT_NOTIFICATION_ID
import com.example.smombierecognitionalarmapplication.data.local.PreferenceUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val prefUtil = PreferenceUtils(applicationContext)
        prefUtil.setUserToken(token)
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val gotoMain = PendingIntent.getActivity(
            this,
            CUSTOM_REQUEST_CODE_MAIN,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, DANGERALERT_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("FCM Message")
            .setContentText(message.toString())
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(gotoMain)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(DANGERALERT_NOTIFICATION_ID, notification)
    }
}