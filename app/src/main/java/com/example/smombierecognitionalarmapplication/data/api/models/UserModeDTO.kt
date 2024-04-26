package com.example.smombierecognitionalarmapplication.data.api.models

import com.google.gson.annotations.SerializedName

data class UserModeDTO(
    @SerializedName("deviceId")
    val deviceId : String,
    @SerializedName("mode")
    val mode : Boolean
)