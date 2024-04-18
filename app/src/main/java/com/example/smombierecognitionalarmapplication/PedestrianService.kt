package com.example.smombierecognitionalarmapplication

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.smombierecognitionalarmapplication.utils.CUSTOM_INTENT_USER_ACTION
import com.example.smombierecognitionalarmapplication.utils.RUNNING_NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PedestrianService(context : Context) : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var sensorBasedMotionProvider = SensorBasedMotionProvider()
    private var locationBasedMotionProvider = LocationBasedMotionProvider()
    private var userActivityTransitionManager = UserActivityTransitionManager(context)

    private val broadcast_activityTransition = UserActivityTransitionBroadcastReceiver()
    private val intentFilter_activityTransition = IntentFilter(CUSTOM_INTENT_USER_ACTION)

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationBasedMotionProvider.initServiceManager(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Start")
            .setContentText("Start App ...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        locationBasedMotionProvider.registerServiceListener()
        locationBasedMotionProvider
            .getMovementUpdates()
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString().take(5)
                val long = location.longitude.toString().take(5)
                val orientation = location.bearing.toString()
                val speed = location.speed.toString()

                //Modified by HTTP Method :: REQUIRED
                val updatedNotification = notification.setContentText(
                    "Location : ($lat, $long)"
                    + "\nOrientation : $orientation"
                    + "\nspeed : $speed"
                )
                notificationManager.notify(RUNNING_NOTIFICATION_ID, updatedNotification.build())
            }.launchIn(serviceScope)

        startForeground(RUNNING_NOTIFICATION_ID, notification.build())
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}