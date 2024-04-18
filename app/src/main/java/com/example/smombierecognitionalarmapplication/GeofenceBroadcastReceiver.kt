package com.example.smombierecognitionalarmapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent


class GeofenceBroadcastReceiver : BroadcastReceiver(){
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

//        if(geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
//            val workRequest = OneTimeWorkRequestBuilder<GeofenceWorker>().build()
//            WorkManager.getInstance(context).enqueue(workRequest)
//        }
    }

}