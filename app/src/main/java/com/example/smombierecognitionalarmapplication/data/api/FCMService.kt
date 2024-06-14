package com.example.smombierecognitionalarmapplication.data.api

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.smombierecognitionalarmapplication.R
import com.example.smombierecognitionalarmapplication.data.CUSTOM_REQUEST_CODE_CONFIRM
import com.example.smombierecognitionalarmapplication.data.DANGERALERT_NOTIFICATION_CHANNEL_ID
import com.example.smombierecognitionalarmapplication.data.DANGERALERT_NOTIFICATION_ID
import com.example.smombierecognitionalarmapplication.data.local.PreferenceUtils
import com.example.smombierecognitionalarmapplication.data.notification.NotificationConfirmReceiver
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val prefUtil = PreferenceUtils(applicationContext)
        prefUtil.setUserToken(token)
    }

    @SuppressLint("LaunchActivityFromNotification")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if(NotificationConfirmReceiver.checkNotificationConfirmRecently()){
            return
        }

        val confirmIntent = Intent(this, NotificationConfirmReceiver::class.java).apply {
            action = "CONFIRM_NOTIFICATION"
        }
        val confirmPendingIntent = PendingIntent.getBroadcast(
            this,
            CUSTOM_REQUEST_CODE_CONFIRM,
            confirmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, DANGERALERT_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("주의!")
            .setContentText("근처에 충돌 가능성이 높은 차량이 있습니다!\n" +
                    "주의 하세요!")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(1000, 500, 1000))
            .setContentIntent(confirmPendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(DANGERALERT_NOTIFICATION_ID, notification)
    }
}