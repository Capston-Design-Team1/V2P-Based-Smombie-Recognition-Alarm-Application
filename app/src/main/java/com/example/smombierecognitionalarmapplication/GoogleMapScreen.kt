package com.example.smombierecognitionalarmapplication

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class GoogleMapScreen : ComponentActivity() {

    lateinit var locationPermission: ActivityResultLauncher<Array<String>>

    lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var locationCallback: LocationCallback

    lateinit var lastLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()){
                results->
            if(results.all{it.value}){
                updateLocation()
            }else{
                Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show()
            }
        }
        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

    }

    @SuppressLint("MissingPermission")
    fun updateLocation(){
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()
        locationCallback = object:LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.let{
                    for(location in it.locations){
                        lastLocation = LatLng(location.latitude,location.longitude)
                        Log.d("current location","$lastLocation")
                        setContent{
                            MapScreenWithMarker(lastLocation,null)
                        }
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
    }
}

@Composable
fun MapScreenWithMarker(currentLocation:LatLng, smombies: List<SmombiesDTO>?) {
    // change color of the marker
    // implement onClick with popup composable

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 30f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = currentLocation),
            title = "Current location"
        )
        // 왜 smombieDTo가 아니라 smombiesDTo를 사용할까?
        // smombie마다 risklevel이 다른데 같이 해도 되나?
        smombies?.forEach { smombie ->
            RiskMarker(
                position = LatLng(
                    smombie.smombieLocationListDTO[0].latitude,
                    smombie.smombieLocationListDTO[0].longitude),
                title = "Smombie",
                snippet = "Risk Level :"+smombie.riskLevel,
                risk = smombie.riskLevel)
        }
    }
}

@Composable
fun RiskMarker(
    position: LatLng,
    title: String,
    snippet: String,
    risk: Int
) {
    val hue = risk.toFloat()+200
    val icon = BitmapDescriptorFactory.defaultMarker(hue)
    Marker(
        state = MarkerState(position=position),
        title = title,
        snippet = snippet,
        icon = icon,
    )
}
