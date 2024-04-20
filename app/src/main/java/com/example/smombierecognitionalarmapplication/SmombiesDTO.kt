package com.example.smombierecognitionalarmapplication

import com.google.gson.annotations.SerializedName

data class SmombiesDTO(
    @SerializedName("riskLevel")
    val riskLevel: Int,
    @SerializedName("smombieLocation")
    val smombieLocationListDTO : List<LocationDTO>
)