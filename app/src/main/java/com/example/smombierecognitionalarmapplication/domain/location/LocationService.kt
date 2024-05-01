package com.example.smombierecognitionalarmapplication.domain.location

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smombierecognitionalarmapplication.MainActivity
import com.example.smombierecognitionalarmapplication.R
import com.example.smombierecognitionalarmapplication.data.CUSTOM_REQUEST_CODE_MAIN
import com.example.smombierecognitionalarmapplication.data.LOCATION_NOTIFICATION_CHANNEL_ID
import com.example.smombierecognitionalarmapplication.data.LOCATION_NOTIFICATION_ID
import com.example.smombierecognitionalarmapplication.data.location.LocationBasedMotionProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
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
        private val _locationUpdate = MutableSharedFlow<Location>(
            extraBufferCapacity = 2,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        val locationUpdate = _locationUpdate.asSharedFlow()
    }
    override fun onBind(intent : Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Location Service$startId $flags", "start")
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

        val notification = NotificationCompat.Builder(this, LOCATION_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(gotoMain)
            .setOngoing(true)

        locationBasedMotionProvider.initServiceManager(this)
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
                Log.d("notification", "Location : ($lat, $long)\n" +
                        "Orientation : $orientation\n" +
                        "Speed : $speed")
                _locationUpdate.emit(location)
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