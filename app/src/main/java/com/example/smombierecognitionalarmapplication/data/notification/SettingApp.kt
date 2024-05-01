package com.example.smombierecognitionalarmapplication.data.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.example.smombierecognitionalarmapplication.data.LOCATION_NOTIFICATION_CHANNEL_ID
import com.example.smombierecognitionalarmapplication.data.SMOMBIEALERT_NOTIFICATION_CHANNEL_ID

class SettingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val locationChannel = NotificationChannel(
            LOCATION_NOTIFICATION_CHANNEL_ID,
            "Location",
            NotificationManager.IMPORTANCE_NONE
        )
        val locationNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        locationNotificationManager.createNotificationChannel(locationChannel)

        val smombieAlertChannel = NotificationChannel(
            SMOMBIEALERT_NOTIFICATION_CHANNEL_ID,
            "SmombieAlert",
            NotificationManager.IMPORTANCE_HIGH
        )
        val smombieNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        smombieNotificationManager.createNotificationChannel(smombieAlertChannel)
    }
}