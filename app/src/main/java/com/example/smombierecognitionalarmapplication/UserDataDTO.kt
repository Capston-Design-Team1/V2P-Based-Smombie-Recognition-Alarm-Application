package com.example.smombierecognitionalarmapplication

import com.google.gson.annotations.SerializedName

data class UserDataDTO(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("direction")
    val direction: Double,
    @SerializedName("mode")
    val mode: Boolean,
    @SerializedName("smombie")
    val smombie: Boolean,
    @SerializedName("apName")
    val apName: String
)