package com.example.smombierecognitionalarmapplication.ui.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smombierecognitionalarmapplication.data.api.RetrofitManager
import com.example.smombierecognitionalarmapplication.domain.location.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.flow.map

@Composable
fun MapScreen(activity: ComponentActivity) {
    val APPosition = LatLng(37.4221, -122.0852) // Modify Required
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(APPosition, 15f)
    }

    val currentLocation = LocationService.locationUpdate
        .map { it?.let { LatLng(it.latitude, it.longitude) } }
        .collectAsStateWithLifecycle(initialValue = null).value
    val smombiesLocation = RetrofitManager.smombieUpdate
        .map { smombiesLocation ->
            val smombieMarkers = mutableMapOf<String, Pair<LatLng, Int>>()
            listOf(
                smombiesLocation?.riskLevel1 to 1,
                smombiesLocation?.riskLevel2 to 2,
                smombiesLocation?.riskLevel3 to 3
            ).forEach { (riskLevelInfos, riskLevel) ->
                riskLevelInfos?.forEach { info ->
                    smombieMarkers[info.deviceId] =
                        Pair(LatLng(info.latitude, info.longitude), riskLevel)
                    Log.d("DeviceID : ${info.deviceId}", "Location : ${info.latitude}, ${info.longitude}, $riskLevel")
                }
            }
            smombieMarkers
        }
        .collectAsStateWithLifecycle(initialValue = emptyMap()).value
    //여러 스몸비의 위치가 동시에 포착 되지 못함... 

    val currentLocationMarkerState = rememberMarkerState(position = APPosition)

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            currentLocationMarkerState.position = it
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(it, 14f)
            )
        }
    }
    LaunchedEffect(smombiesLocation) {

    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        APMarker(
            position = APPosition,
            title = "AP",
            snippet = "current AP"
        )
        Marker(
            state = currentLocationMarkerState,
            title = "Current Location"
        )
        smombiesLocation.forEach { (deviceId, pair) ->
            SmombieMarker(
                position = pair.first,
                title = "스몸비 $deviceId",
                snippet = "스몸비",
                risk = pair.second
            )
        }
    }

    AlarmDisplay()
}

@Composable
private fun SmombieMarker(
    position: LatLng,
    title: String,
    snippet: String,
    risk: Int
) {
    val icon = when (risk) {
        1 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        2 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        3 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        else -> return
    }
    Marker(
        state = MarkerState(position = position),
        title = title,
        snippet = snippet,
        icon = icon,
    )
}

@Composable
private fun APMarker(
    position: LatLng,
    title: String,
    snippet: String,
) {
    val hue = 50f
    val icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
    Marker(
        state = MarkerState(position = position),
        title = title,
        snippet = snippet,
        icon = icon,
    )
}

@Composable
private fun AlarmDisplay() {
    val alarm = RetrofitManager.alarm.collectAsStateWithLifecycle(initialValue = false).value

    if (alarm) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "경고",
            tint = Color.Yellow,
            modifier = Modifier.size(200.dp)
        )
    }
}
