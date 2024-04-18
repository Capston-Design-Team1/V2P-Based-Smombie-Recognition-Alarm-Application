package com.example.smombierecognitionalarmapplication

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.example.smombierecognitionalarmapplication.utils.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationBasedMotionProvider() : MovementAnalyzer{
    private lateinit var context: Context
    private lateinit var fusedlocationManager : FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest


    @SuppressLint("MissingPermission")
    override fun getMovementUpdates() : Flow<Location> {
        return callbackFlow {
            if(!context.hasLocationPermission()) {
                throw MovementAnalyzer.Exceptions("Missing location permission")
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) }
                    }
                }
            }
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(!isGpsEnabled && !isNetworkEnabled) {
                throw MovementAnalyzer.Exceptions("GPS is disabled")
            }
            fusedlocationManager.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper() //Modify required
            )

            awaitClose {
                fusedlocationManager.removeLocationUpdates(locationCallback)
            }
        }
    }

    override fun initServiceManager(context: Context) {
        fusedlocationManager = LocationServices.getFusedLocationProviderClient(context)
        this.context = context
    }

    override fun registerServiceListener() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
    }

    override fun unregisterServiceListener() {
        TODO("Not yet implemented")
    }

}