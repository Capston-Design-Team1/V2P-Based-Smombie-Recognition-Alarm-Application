package com.example.smombierecognitionalarmapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.example.smombierecognitionalarmapplication.ui.theme.SmombieRecognitionAlarmApplicationTheme
import com.example.smombierecognitionalarmapplication.utils.CUSTOM_INTENT_GEOFENCE
import com.example.smombierecognitionalarmapplication.utils.PreferenceUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var geofenceManager: GeofenceManager
    private val broadcast_geofence = GeofenceBroadcastReceiver()
    private val intentFilter_geofence = IntentFilter(CUSTOM_INTENT_GEOFENCE)
    private lateinit var locationCient : FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
        val prefUtil = PreferenceUtils(applicationContext)
        locationCient = LocationServices.getFusedLocationProviderClient(this)
        geofenceManager = GeofenceManager(applicationContext)
        setContent {
            SmombieRecognitionAlarmApplicationTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(onClick = {
                        prefUtil.setUserMode(true)
                        if (geofenceManager.geofenceList.isNotEmpty()) {
                            try{
                                applicationContext.registerReceiver(broadcast_geofence, intentFilter_geofence)
                            }catch (e : Exception){
                                e.printStackTrace()
                            }finally {
                                Log.d("GeofenceManager", "register")
                            }
                            geofenceManager.registerGeofence()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Please add at least one geofence",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }) {
                        Text(text = "Pedestrian")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        prefUtil.setUserMode(false)
                        lifecycleScope.launch{
                            geofenceManager.deregisterGeofence()
                        }
                    }) {
                        Text(text = "Vehicle")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        geofenceManager.addGeofence(
                            "mountain_view",
                            location = Location("").apply {
                                latitude = 37.4219983
                                longitude = -122.084
                            },
                        )
                    }) {
                        Text(text = "add geofence")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

}