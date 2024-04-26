package com.example.smombierecognitionalarmapplication.data.api.models

import com.google.gson.annotations.SerializedName

data class LocationDTO(
    @SerializedName("deviceId")
    val deviceId : String,
    @SerializedName("latitude")
    val latitude : Double,
    @SerializedName("longitude")
    val longitude : Double
)
