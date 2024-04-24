package com.example.smombierecognitionalarmapplication

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.smombierecognitionalarmapplication.utils.LOCATION_DB_NOTIFICATION_ID
import com.example.smombierecognitionalarmapplication.utils.LOCATION_NOTIFICATION_CHANNEL_ID
import com.example.smombierecognitionalarmapplication.utils.PreferenceUtils
import com.example.smombierecognitionalarmapplication.utils.checkMemoryUsageHigh
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PedestrianService : Service() {
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

        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun start() {
        val notification = NotificationCompat.Builder(this, LOCATION_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Pedestrian Service")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
        val retrofitManager = RetrofitManager()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val prefUtil = PreferenceUtils(applicationContext)

        val apName = "newAP" // Modify Required
        serviceScope.launch {
            LocationService.locationUpdate.collect{ location ->
                if(ScreenStateReceiver.isScreenOn and checkMemoryUsageHigh(applicationContext)){
                    val userDataDTO = UserDataDTO(location, true, true, apName)
                    retrofitManager.patchUserData(prefUtil.getUuid(), userDataDTO)
                } else {
                    val userDataDTO = UserDataDTO(location, true, false, apName)
                    retrofitManager.patchUserData(prefUtil.getUuid(), userDataDTO)
                }
            }
        }

        notificationManager.notify(LOCATION_DB_NOTIFICATION_ID, notification.build())
        startForeground(LOCATION_DB_NOTIFICATION_ID, notification.build())
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        running = false
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        running = false
    }
}