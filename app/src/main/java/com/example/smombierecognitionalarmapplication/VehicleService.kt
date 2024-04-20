package com.example.smombierecognitionalarmapplication

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smombierecognitionalarmapplication.utils.APIBASE_URL
import com.example.smombierecognitionalarmapplication.utils.LOCATION_DB_NOTIFICATION_ID
import com.example.smombierecognitionalarmapplication.utils.LOCATION_NOTIFICATION_CHANNEL_ID
import com.example.smombierecognitionalarmapplication.utils.PreferenceUtils
import com.example.smombierecognitionalarmapplication.utils.SMOMBIEALERT_NOTIFICATION_CHANNEL_ID
import com.example.smombierecognitionalarmapplication.utils.SMOMBIEALERT_NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
        return START_STICKY
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, SMOMBIEALERT_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Vehicle Service")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val retrofitManager = RetrofitManager()
        val userId = PreferenceUtils(applicationContext).getUuid()
        notificationManager.getNotificationChannel(SMOMBIEALERT_NOTIFICATION_CHANNEL_ID)
        serviceScope.launch{
            LocationService.locationUpdate.collect{ location ->
                val smombieDataList = retrofitManager.getSmombieData(userId)
                Log.d("Vehicle", "Running" + smombieDataList.toString())
                val updatedNotification = notification.setContentText(
                    location.toString()
                )
                /*
                    특정 사용자에게서 100초 이내로 다시 알림 받을 수 없도록 만들기.
                 */
                notificationManager.notify(SMOMBIEALERT_NOTIFICATION_ID, updatedNotification.build())
            }
        }

        startForeground(SMOMBIEALERT_NOTIFICATION_ID, notification.build())
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