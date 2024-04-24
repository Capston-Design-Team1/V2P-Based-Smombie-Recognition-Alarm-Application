package com.example.smombierecognitionalarmapplication

import com.google.gson.annotations.SerializedName

data class LocationDTO(
    @SerializedName("deviceId")
    val deviceId : String,
    @SerializedName("latitude")
    val latitude : Double,
    @SerializedName("longitude")
    val longitude : Double
)
