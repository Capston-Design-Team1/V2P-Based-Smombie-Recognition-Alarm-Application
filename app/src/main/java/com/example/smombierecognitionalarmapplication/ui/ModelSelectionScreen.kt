package com.example.smombierecognitionalarmapplication.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.smombierecognitionalarmapplication.data.local.PreferenceUtils

@Composable
fun ModeSelectionScreen(navController: NavController, activity: ComponentActivity) {
    var permissionsGranted by remember { mutableStateOf(false) }

    val permissions = mutableListOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CHANGE_WIFI_STATE,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.INTERNET
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permissions.addAll(
            listOf(
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                android.Manifest.permission.FOREGROUND_SERVICE,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            )
        )
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    permissionsGranted = permissions.all { permission ->
        ContextCompat.checkSelfPermission(activity, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    if(!permissionsGranted) {
        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) {
                results ->
            permissionsGranted = results.all { it.value }
        }
        AlertDialog(
            onDismissRequest = { ActivityCompat.finishAffinity(activity) },
            title = { Text("권한 필요") },
            text = { Text("이 앱은 다음 기능을 사용하기 위해 권한이 필요합니다: 위치, 와이파이 상태 변경, 인터넷, 백그라운드 위치 접근. 권한을 허용해주세요.") },
            confirmButton = {
                Button(
                    onClick = {
                        requestPermissionLauncher.launch(permissions.toTypedArray())
                        showSettingsDialog(activity)
                    }
                ) {
                    Text("권한 요청")
                }
            },
            dismissButton = {
                Button(onClick = { ActivityCompat.finishAffinity(activity) }) {
                    Text("나중에")
                }
            }
        )
    }

    if (permissionsGranted) {
        NavigationButtons(navController, activity)
    }
}

@Composable
fun NavigationButtons(navController: NavController, activity: ComponentActivity) {
    val prefUtil = PreferenceUtils(activity.applicationContext)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                prefUtil.setUserMode(true)
                navController.navigate("pedestrian")
            },
            modifier = Modifier.height(200.dp)
        ) {
            Text("보행자 모드", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            onClick = {
                prefUtil.setUserMode(false)
                navController.navigate("vehicle")
            },
            modifier = Modifier.height(200.dp)
        ) {
            Text("운전자 모드", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

private fun showSettingsDialog(activity: ComponentActivity) {
    activity.startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
    )
}