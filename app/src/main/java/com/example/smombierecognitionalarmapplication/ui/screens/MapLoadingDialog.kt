package com.example.smombierecognitionalarmapplication.ui.screens

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smombierecognitionalarmapplication.domain.location.LocationService
import com.example.smombierecognitionalarmapplication.domain.vehicle.VehicleService
import com.google.android.gms.maps.MapsInitializer
import kotlinx.coroutines.delay

@Composable
fun MapLoadingDialog(activity: ComponentActivity, navController: NavController){
    LaunchedEffect(Unit) {
        if(!LocationService.isRunning()){
            startLocationService(activity.applicationContext)
        }
        if(!VehicleService.isRunning()){
            startVehicleService(activity.applicationContext)
        }
        MapsInitializer.initialize(activity, MapsInitializer.Renderer.LATEST, null)
        // Modify Required
        delay(2000)
        navController.navigate("map")
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(text = "지도 화면 로딩 중...", modifier = Modifier.padding(top = 16.dp))
        }
    }
}

private fun startLocationService(context : Context){
    Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_START
        context.startForegroundService(this)
    }
}

private fun startVehicleService(context: Context){
    Intent(context, VehicleService::class.java).apply {
        action = VehicleService.ACTION_START
        context.startForegroundService(this)
    }
}