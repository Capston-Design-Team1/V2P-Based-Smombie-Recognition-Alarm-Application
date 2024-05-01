package com.example.smombierecognitionalarmapplication.deprecated

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smombierecognitionalarmapplication.data.local.PreferenceUtils
import com.example.smombierecognitionalarmapplication.domain.pedestrian.PedestrianService
import com.example.smombierecognitionalarmapplication.domain.vehicle.VehicleService

@Deprecated("GeofenceWorker")
class GeofenceWorker(appContext: Context, workerParams : WorkerParameters) :
    CoroutineWorker(appContext, workerParams){

    override suspend fun doWork(): Result {
        runCatching {
            when(PreferenceUtils.getUserMode()){
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