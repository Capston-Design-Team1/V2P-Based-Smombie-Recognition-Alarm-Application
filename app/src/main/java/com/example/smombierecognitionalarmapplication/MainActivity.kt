package com.example.smombierecognitionalarmapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.smombierecognitionalarmapplication.domain.location.LocationService
import com.example.smombierecognitionalarmapplication.domain.pedestrian.PedestrianService
import com.example.smombierecognitionalarmapplication.domain.vehicle.VehicleService
import com.example.smombierecognitionalarmapplication.ui.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppNavigation(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Screen", "Destroy")
        stopLocationService(this.applicationContext)
        stopVehicleService(this.applicationContext)
        stopPedestrianService(this.applicationContext)
    }

    private fun stopLocationService(context : Context){
        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            context.stopService(this)
        }
    }

    private fun stopVehicleService(context: Context){
        Intent(context, VehicleService::class.java).apply {
            action = VehicleService.ACTION_STOP
            context.stopService(this)
        }
    }

    private fun stopPedestrianService(context: Context){
        Intent(context, PedestrianService::class.java).apply {
            action = PedestrianService.ACTION_STOP
            context.stopService(this)
        }
    }
}



