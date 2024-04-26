package com.example.smombierecognitionalarmapplication.data.api.models

import android.location.Location
import com.google.gson.annotations.SerializedName

data class UserDataDTO(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("speed")
    val speed: Float,
    @SerializedName("direction")
    val direction: Float,
    @SerializedName("mode")
    val mode: Boolean,
    @SerializedName("smombie")
    val smombie: Boolean,
    @SerializedName("apName")
    val apName: String
){
    constructor(location: Location, mode: Boolean, isSmombie: Boolean, apName: String) :
            this(
                latitude = location.latitude,
                longitude = location.longitude,
                speed = location.speed,
                direction = location.bearing,
                mode = mode,
                smombie = isSmombie,
                apName = apName
            )
}
