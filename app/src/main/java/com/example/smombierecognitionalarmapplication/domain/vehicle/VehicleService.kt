package com.example.smombierecognitionalarmapplication.domain.vehicle

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.smombierecognitionalarmapplication.MainActivity
import com.example.smombierecognitionalarmapplication.R
import com.example.smombierecognitionalarmapplication.data.CUSTOM_REQUEST_CODE_MAIN
import com.example.smombierecognitionalarmapplication.data.LOCATION_NOTIFICATION_CHANNEL_ID
import com.example.smombierecognitionalarmapplication.data.LOCATION_NOTIFICATION_ID
import com.example.smombierecognitionalarmapplication.data.SMOMBIEALERT_NOTIFICATION_CHANNEL_ID
import com.example.smombierecognitionalarmapplication.data.SMOMBIEALERT_NOTIFICATION_ID
import com.example.smombierecognitionalarmapplication.data.api.RetrofitManager
import com.example.smombierecognitionalarmapplication.data.api.models.UserDataDTO
import com.example.smombierecognitionalarmapplication.data.local.PreferenceUtils
import com.example.smombierecognitionalarmapplication.domain.location.LocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VehicleService : Service(){
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        private var running = false
        fun isRunning() : Boolean{
            return running
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent == null){
            running = true
            start()
        } else {
            when (intent.action) {
                ACTION_START -> {
                    running = true
                    start()
                }
                ACTION_STOP -> {
                    running = false
                    stop()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun start() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val gotoMain = PendingIntent.getActivity(
            this,
            CUSTOM_REQUEST_CODE_MAIN,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alertNotification = NotificationCompat.Builder(this, SMOMBIEALERT_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Alert")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(gotoMain)
            .setAutoCancel(true)

        val locationNotification = NotificationCompat.Builder(this, LOCATION_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Vehicle Service")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(gotoMain)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.getNotificationChannel(LOCATION_NOTIFICATION_CHANNEL_ID)
        val apName = "newAP" // Modify Required
        serviceScope.launch{
            LocationService.locationUpdate.collect{ location ->
                val userDataDTO = UserDataDTO(location, PreferenceUtils.getUserMode(), false, apName)
                RetrofitManager.patchUserData(userDataDTO)

                val alert = try {
                    RetrofitManager.getSmombieData()
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }

                if (alert) {
                    withContext(Dispatchers.Main) {
                        val updatedNotification = alertNotification.setContentText(
                            location.toString()
                        )
                        notificationManager.notify(
                            SMOMBIEALERT_NOTIFICATION_ID,
                            updatedNotification.build()
                        )
                    }
                }
            }
        }

        startForeground(LOCATION_NOTIFICATION_ID, locationNotification.build())
    }

    private fun stop() {
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        stopSelf()
        running = false
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        running = false
    }
}