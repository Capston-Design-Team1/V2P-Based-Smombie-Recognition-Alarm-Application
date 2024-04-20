package com.example.smombierecognitionalarmapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GoogleMap : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapScreen(CurrentLocationService(this))
        }
    }
}

interface ILocationService {
    fun requestCurrentLocation(): Flow<LatLng?>
}

class CurrentLocationService(private val context: Context) : ILocationService {
    private val locationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun requestCurrentLocation(): Flow<LatLng?> = callbackFlow {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    trySend(LatLng(it.latitude, it.longitude))
                }
            }
        } else {
            trySend(null)
        }
        awaitClose { }
    }
}

@Composable
fun MapScreen(locationService: ILocationService) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()

    LaunchedEffect(Unit) {
        locationService.requestCurrentLocation().collect { currentLocation ->
            mapView.getMapAsync { googleMap ->
                currentLocation?.let {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                    googleMap.addMarker(MarkerOptions().position(it).title("Current Location"))
                }
            }
        }
    }

    AndroidView({ mapView }) { mapView ->
        mapView.onResume()
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
        }
    }
    AndroidView({ mapView }) { mapView ->
        mapView.onResume()
    }
    return mapView
}


