package com.example.smombierecognitionalarmapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.smombierecognitionalarmapplication.utils.PreferenceUtils
import com.example.smombierecognitionalarmapplication.utils.hasLocationPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var geofenceManager: GeofenceManager

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//            ),
//            0
//        )

        var permissionArray = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        //if above permissions are agreed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionArray += Manifest.permission.POST_NOTIFICATIONS
        }
        permissionArray.forEach {
            permission -> Log.d("permission", permission)
        }

        ActivityCompat.requestPermissions(this, permissionArray, 0)

        val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var deniedPermissions = ""
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if(!isGranted) {
                    deniedPermissions += permissionName
                }
            }
            Log.d("permission",deniedPermissions)
            if(deniedPermissions.isNotEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "$deniedPermissions" + " - 권한을 허용해 주세요",
                    Toast.LENGTH_SHORT,
                ).show()
                onDestroy()
            }
        }

//        requestPermissionsLauncher.launch(
//            permissionArray
//        )

        val prefUtil = PreferenceUtils(applicationContext)

        geofenceManager = GeofenceManager(this)
        if(!LocationService.isRunning() and hasLocationPermission()){
            startLocationService()
        }
        setContent {
            SmombieRecognitionAlarmApplicationTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(onClick = {
                        prefUtil.setUserMode(false)
                        if (geofenceManager.geofenceList.isNotEmpty() and hasLocationPermission()) {
                            CoroutineScope(Job() + Dispatchers.IO).launch {
                                geofenceManager.registerGeofence()
                            }
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "백그라운드 위치 권한을 허용해 주세요",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }) {
                        Text(text = "Pedestrian")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        prefUtil.setUserMode(false)
                        if (hasLocationPermission()) {
                            CoroutineScope(Job() + Dispatchers.IO).launch {
                                geofenceManager.deregisterGeofence()
                            }
                            stopLocationService()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "백그라운드 위치 권한을 허용해 주세요",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }) {
                        Text(text = "Vehicle")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        geofenceManager.addGeofence(
                            "mountain_view",
                            location = Location("").apply {
                                latitude = 37.4221
                                longitude = -122.0852
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
    private fun startLocationService(){
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            applicationContext.startForegroundService(this)
        }
    }

    private fun stopLocationService(){
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            applicationContext.startForegroundService(this)
        }
    }
}