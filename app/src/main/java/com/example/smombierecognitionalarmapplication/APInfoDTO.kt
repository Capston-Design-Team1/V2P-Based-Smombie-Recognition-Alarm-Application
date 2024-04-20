package com.example.smombierecognitionalarmapplication

import com.google.gson.annotations.SerializedName

data class APInfoDTO(
    @SerializedName("name")
    val name: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)
