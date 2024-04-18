package com.example.smombierecognitionalarmapplication

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import com.example.smombierecognitionalarmapplication.utils.CUSTOM_INTENT_GEOFENCE
import com.example.smombierecognitionalarmapplication.utils.CUSTOM_REQUEST_CODE_GEOFENCE
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class GeofenceManager(context: Context) {
    companion object {
        const val RADIUS_METERS : Float = 100.0f // Default 100m
        const val EXPIRATION_TIME_MILLIS : Long = 30 * 60 * 1000
        const val DWELLING_DELAY_MILLIS : Int = 5000 // 5sec
    }
    private val geofencingClient = LocationServices.getGeofencingClient(context)
    val geofenceList = mutableMapOf<String, Geofence>()

    private val geofencingPendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            CUSTOM_REQUEST_CODE_GEOFENCE,
            Intent(CUSTOM_INTENT_GEOFENCE),
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                PendingIntent.FLAG_CANCEL_CURRENT
            } else {
                PendingIntent.FLAG_MUTABLE
            }
        )
    }

    fun addGeofence(
        key : String,
        location : Location
    ){
        geofenceList[key] = createGeofence(key, location)
        Log.d("GeofenceManager", "addsucceed")
    }

    fun removeGeofence(key: String) {
        geofenceList.remove(key)
    }

    @SuppressLint("MissingPermission")
    fun registerGeofence() {
        geofencingClient.addGeofences(getGeofencingRequest(), geofencingPendingIntent)
            .addOnSuccessListener {
                Log.d("GeofenceManager", "registerGeofence: SUCCESS")
            }.addOnFailureListener { exception ->
                Log.d("GeofenceManager", "registerGeofence: Failure\n$exception")
            }
    }

    suspend fun deregisterGeofence() = runCatching {
        geofencingClient.removeGeofences(geofencingPendingIntent).await()
        geofenceList.clear()
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
            addGeofences(geofenceList.values.toList())
        }.build()
    }

    private fun createGeofence(
        key: String,
        location: Location,
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(key)
            .setCircularRegion(location.latitude, location.longitude, RADIUS_METERS)
            .setExpirationDuration(EXPIRATION_TIME_MILLIS)
            .setTransitionTypes(GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_DWELL or GEOFENCE_TRANSITION_EXIT)
            .setLoiteringDelay(DWELLING_DELAY_MILLIS)
            .build()
    }
}