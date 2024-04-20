package com.example.smombierecognitionalarmapplication

import com.google.gson.annotations.SerializedName

data class UserModeDTO(
    @SerializedName("deviceId")
    val deviceId : String,
    @SerializedName("mode")
    val mode : Boolean
)