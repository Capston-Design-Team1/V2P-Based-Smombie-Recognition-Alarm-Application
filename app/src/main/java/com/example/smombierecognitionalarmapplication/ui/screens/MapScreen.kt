package com.example.smombierecognitionalarmapplication.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun MapScreen(activity: ComponentActivity) {
    val APPosition = LatLng(37.4221, -122.0852) //Modify Required
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(APPosition, 15f)
    }

    val currentLocation= LocationService.locationUpdate.collectAsState(initial = null).value
    val smombiesLocation = RetrofitManager.smombieUpdate.collectAsState(initial = null).value
    val currentLocationMarkerState = rememberMarkerState(position = APPosition)
    val smombieMarkers = remember { mutableStateMapOf<String, Pair<LatLng, Int>>() }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            currentLocationMarkerState.position = LatLng(it.latitude, it.longitude)
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.latitude,
                        it.longitude
                    ), 17f
                )
            )
        }
    }

    LaunchedEffect(smombiesLocation) {
        withContext(Dispatchers.Default) {
            listOf(
                smombiesLocation?.riskLevel1 to 1,
                smombiesLocation?.riskLevel2 to 2,
                smombiesLocation?.riskLevel3 to 3
            ).forEach { (riskLevelInfos, riskLevel) ->
                riskLevelInfos?.let { infos ->
                    launch {
                        infos.forEach { info ->
                            smombieMarkers[info.deviceId] =
                                Pair(LatLng(info.latitude, info.longitude), riskLevel)
                        }
                    }
                }
            }
        }
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
        smombieMarkers.forEach { (deviceId, pair) ->
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
    val hue = risk.toFloat()*10+200
    val icon = BitmapDescriptorFactory.defaultMarker(hue)
    Marker(
        state = MarkerState(position=position),
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
    val icon = BitmapDescriptorFactory.defaultMarker(hue)
    Marker(
        state = MarkerState(position=position),
        title = title,
        snippet = snippet,
        icon = icon,
    )
}

@Composable
private fun AlarmDisplay(){
    val alarm = RetrofitManager.alarm.collectAsState(initial = false).value

    if(alarm){
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "경고",
            tint = Color.Yellow,
            modifier = Modifier.size(200.dp)
        )
    }
}