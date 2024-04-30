package com.example.smombierecognitionalarmapplication.data.api.models

import com.google.gson.annotations.SerializedName

data class SmombiesDTO(
    @SerializedName("riskLevel1")
    val riskLevel1: List<LocationDTO>,
    @SerializedName("riskLevel2")
    val riskLevel2: List<LocationDTO>,
    @SerializedName("riskLevel3")
    val riskLevel3: List<LocationDTO>
)