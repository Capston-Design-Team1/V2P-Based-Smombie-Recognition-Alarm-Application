package com.example.smombierecognitionalarmapplication

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smombierecognitionalarmapplication.utils.LOCATION_NOTIFICATION_CHANNEL_ID
import com.example.smombierecognitionalarmapplication.utils.LOCATION_NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService : Service(){
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var locationBasedMotionProvider = LocationBasedMotionProvider()


    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        private var running = false
        fun isRunning(): Boolean {
            return running
        }
        private val _locationUpdate = MutableSharedFlow<Location>()
        val locationUpdate = _locationUpdate.asSharedFlow()
    }
    override fun onBind(intent : Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("$flags", "$startId")
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

    private fun start() {
        val notification = NotificationCompat.Builder(this, LOCATION_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Location")
            .setContentText("IDLE")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        locationBasedMotionProvider.initServiceManager(this)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(LOCATION_NOTIFICATION_ID, notification.build())
        locationBasedMotionProvider.registerServiceListener()
        locationBasedMotionProvider
            .getMovementUpdates()
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString().take(5)
                val long = location.longitude.toString().take(5)
                val orientation = location.bearing.toString()
                val speed = location.speed.toString()
                Log.d("notification", "\"Location : ($lat, $long)\"\n" +
                        "                    + \"\\nOrientation : $orientation\"\n" +
                        "                    + \"\\nSpeed : $speed\"")
                if(PedestrianService.isRunning()) {
                    _locationUpdate.emit(location)
                    Log.d("Location", "emit")
                }
            }.launchIn(serviceScope)

        startForeground(LOCATION_NOTIFICATION_ID, notification.build())
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