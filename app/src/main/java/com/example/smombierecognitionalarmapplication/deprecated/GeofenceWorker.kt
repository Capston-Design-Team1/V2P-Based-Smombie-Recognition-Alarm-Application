package com.example.smombierecognitionalarmapplication.deprecated

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smombierecognitionalarmapplication.PedestrianService
import com.example.smombierecognitionalarmapplication.VehicleService
import com.example.smombierecognitionalarmapplication.utils.PreferenceUtils

class GeofenceWorker(appContext: Context, workerParams : WorkerParameters) :
    CoroutineWorker(appContext, workerParams){
    private val prefUtil = PreferenceUtils(appContext)

    override suspend fun doWork(): Result {
        runCatching {
            when(prefUtil.getUserMode()){
                //pedestrian
                true -> Intent(applicationContext, PedestrianService::class.java).apply {
                    action = PedestrianService.ACTION_START
                    applicationContext.startForegroundService(this)
                }
                //vehicle
                false -> Intent(applicationContext, VehicleService::class.java).apply {
                    action = PedestrianService.ACTION_START
                    applicationContext.startForegroundService(this)
                }
            }
        }
        return Result.success()
    }
}