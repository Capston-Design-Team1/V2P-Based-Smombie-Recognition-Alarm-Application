package com.example.smombierecognitionalarmapplication.data.api.models

import com.google.gson.annotations.SerializedName

data class SmombiesDTO(
    @SerializedName("riskLevel")
    val riskLevel: Int,
    @SerializedName("smombieLocation")
    val smombieLocationListDTO : List<LocationDTO>
)