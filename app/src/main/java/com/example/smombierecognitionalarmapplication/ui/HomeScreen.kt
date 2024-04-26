package com.example.smombierecognitionalarmapplication.ui

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.smombierecognitionalarmapplication.common.utils.isInternetConnected
import com.example.smombierecognitionalarmapplication.data.api.RetrofitManager
import com.example.smombierecognitionalarmapplication.data.api.models.APInfoDTO
import com.example.smombierecognitionalarmapplication.data.geofence.GeofenceManager
import com.example.smombierecognitionalarmapplication.data.local.PreferenceUtils
import com.example.smombierecognitionalarmapplication.domain.location.LocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(activity: ComponentActivity, userMode : Boolean){
    val geofenceManager = GeofenceManager(activity.applicationContext)
    val retrofitManager = RetrofitManager()
    val prefUtils = PreferenceUtils(activity.applicationContext)
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if(isInternetConnected(activity.applicationContext)){
                    CoroutineScope(Dispatchers.IO).launch {
                        geofenceManager.addGeofence(
                            "mountain_view",
                            location = Location("").apply {
                                latitude = 37.4221
                                longitude = -122.0852
                            },
                        )
                        geofenceManager.registerGeofence()
                        prefUtils.createUuid()
                        delay(500)
                        prefUtils.setUserMode(userMode)
                        retrofitManager.postUserMode()
                        retrofitManager.postAPInfo(APInfoDTO("newAP", 37.4221, -122.0852)) // Modify Required
                    }
                    if(!LocationService.isRunning()){
                        Log.d("Start", "StartLocationService")
                        startLocationService(activity.applicationContext)
                    }
                    sendAppToBackground(activity)
                } else {
                    Toast.makeText(
                        activity.baseContext,
                        "인터넷 연결을 확인해 주세요",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
            modifier = Modifier.height(200.dp)
        ) {
            Text("백그라운드 실행", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            onClick = {
                stopLocationService(activity.applicationContext)
                CoroutineScope(Dispatchers.IO).launch {
                    geofenceManager.deregisterGeofence()
                }
                ActivityCompat.finishAffinity(activity)
            },
            modifier = Modifier.height(200.dp)
        ) {
            Text("종료", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

private fun startLocationService(context : Context){
    Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_START
        context.startForegroundService(this)
    }
}

private fun stopLocationService(context : Context){
    Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_STOP
        context.startForegroundService(this)
    }
}

private fun sendAppToBackground(context: Context){
    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    context.startActivity(homeIntent)
}