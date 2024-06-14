package com.example.smombierecognitionalarmapplication.data.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.example.smombierecognitionalarmapplication.data.CONFIRM_EXPIRATION_TIME
import com.example.smombierecognitionalarmapplication.data.DANGERALERT_NOTIFICATION_ID

class NotificationConfirmReceiver : BroadcastReceiver() {
    companion object {
        private var notificationConfirm = false
        fun checkNotificationConfirmRecently() : Boolean{
            return notificationConfirm
        }
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "CONFIRM_NOTIFICATION") {
            notificationConfirm = true
            Handler(Looper.getMainLooper()).postDelayed({
                notificationConfirm = false
            }, CONFIRM_EXPIRATION_TIME)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(DANGERALERT_NOTIFICATION_ID)
        }
    }
}