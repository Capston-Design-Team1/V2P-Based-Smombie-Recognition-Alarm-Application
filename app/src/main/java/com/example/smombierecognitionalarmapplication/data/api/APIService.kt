package com.example.smombierecognitionalarmapplication.data.api

import com.example.smombierecognitionalarmapplication.data.api.models.APInfoDTO
import com.example.smombierecognitionalarmapplication.data.api.models.SmombiesDTO
import com.example.smombierecognitionalarmapplication.data.api.models.UserDataDTO
import com.example.smombierecognitionalarmapplication.data.api.models.UserModeDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
    @GET("users/{deviceId}/smombies")
    fun getSmombies(@Path("deviceId") deviceId: String) : Call<List<SmombiesDTO>>
    @POST("users")
    fun postUserMode(@Body userMode: UserModeDTO) : Call<Void>
    @POST("ap")
    fun postAPInfo(@Body apInfo : APInfoDTO) : Call<Void>
    @PATCH("users/{deviceId}")
    fun patchUserData(
        @Path("deviceId") deviceId: String,
        @Body userData : UserDataDTO
    ) : Call<Void>
}
