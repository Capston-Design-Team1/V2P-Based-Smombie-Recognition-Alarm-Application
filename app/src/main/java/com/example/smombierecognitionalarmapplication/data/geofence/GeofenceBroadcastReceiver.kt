package com.example.smombierecognitionalarmapplication.data.geofence

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.smombierecognitionalarmapplication.data.local.PreferenceUtils
import com.example.smombierecognitionalarmapplication.data.useractivitytransition.UserActivityTransitionManager
import com.example.smombierecognitionalarmapplication.domain.pedestrian.PedestrianService
import com.example.smombierecognitionalarmapplication.domain.vehicle.VehicleService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GeofenceBroadcastReceiver : BroadcastReceiver(){
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        val geofencingEvent= intent?.let { GeofencingEvent.fromIntent(it) } ?: return

        if(geofencingEvent.hasError()){
            val errorMessage =
                GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e("GeofenceReceiver", "onReceive: $errorMessage")
            return
        }
        val alertString = "Geofence Alert :" +
        " Trigger ${geofencingEvent.triggeringGeofences}" +
                " Transition ${geofencingEvent.geofenceTransition}"
        Log.d(
            "GeofenceReceiver",
            alertString
        )
        if(geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            when(PreferenceUtils.getUserMode()){
                //pedestrian
                true -> {
                    stopVehicleService(context)
                    if (!PedestrianService.isRunning()) {
//                        startPedestrianService(context)
                        CoroutineScope(Dispatchers.IO).launch{
                            UserActivityTransitionManager(context).registerActivityTransitions()
                        }
                    }
                }
                //vehicle
                false -> {
                    stopPedestrianService(context)
                    if (!VehicleService.isRunning()) {
                        startVehicleService(context)
                    }
                    CoroutineScope(Dispatchers.IO).launch{
                        UserActivityTransitionManager(context).deregisterActivityTransitions()
                    }
                }
            }
        }
    }

    private fun startVehicleService(context: Context){
        Intent(context, VehicleService::class.java).apply {
            action = VehicleService.ACTION_START
            context.startService(this)
        }
    }

    private fun stopVehicleService(context: Context){
        Intent(context, VehicleService::class.java).apply {
            action = VehicleService.ACTION_STOP
            context.startService(this)
        }
    }

    private fun startPedestrianService(context: Context){
        Intent(context, PedestrianService::class.java).apply {
            action = PedestrianService.ACTION_START
            context.startService(this)
        }
    }

    private fun stopPedestrianService(context: Context){
        Intent(context, PedestrianService::class.java).apply {
            action = PedestrianService.ACTION_STOP
            context.startService(this)
        }
    }
}