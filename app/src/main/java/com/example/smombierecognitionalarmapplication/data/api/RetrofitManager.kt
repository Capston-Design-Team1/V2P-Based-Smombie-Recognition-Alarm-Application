package com.example.smombierecognitionalarmapplication.data.api

import android.util.Log
import com.example.smombierecognitionalarmapplication.common.utils.HTTPResponseCheck
import com.example.smombierecognitionalarmapplication.data.APIBASE_URL
import com.example.smombierecognitionalarmapplication.data.api.models.APInfoDTO
import com.example.smombierecognitionalarmapplication.data.api.models.UserDataDTO
import com.example.smombierecognitionalarmapplication.data.api.models.UserModeDTO
import com.example.smombierecognitionalarmapplication.data.local.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class RetrofitManager{
    companion object{
        val retrofit by lazy {
            Retrofit.Builder().apply {
                baseUrl(APIBASE_URL)
                addConverterFactory(GsonConverterFactory.create())
            }.build()
        }
        val apiService : APIService by lazy {
            retrofit.create(APIService::class.java)
        }
    }

    suspend fun getSmombieData(){
        val call = apiService.getSmombies(PreferenceUtils.getDeviceID())
        call.awaitResponse().runCatching {
            if(HTTPResponseCheck(code())){
                val responselist = body() ?: return
                withContext(Dispatchers.Main) {
                    responselist.forEach {
                        smombies -> {
                            //UI 업데이트
                        }
                    }
                }
            }
        }.onFailure {
            exception -> exception.printStackTrace()
        }
    }

    suspend fun createUser() : String {
        lateinit var createdUuid : String
        while(true){
            createdUuid = UUID.randomUUID().toString()
            val userModeDTO = UserModeDTO(createdUuid, false) // Default : Vehicle
            val call = apiService.postUserMode(userModeDTO)
            call.awaitResponse().runCatching {
                if(HTTPResponseCheck(code())){
                    return createdUuid
                }
            }.onFailure {
                exception -> exception.printStackTrace()
                delay(500)
            }
        }
    }

    fun postUserMode(){
        val call = apiService.postUserMode(UserModeDTO(PreferenceUtils.getDeviceID(),PreferenceUtils.getUserMode()))
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Server Response", response.code().toString())
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Server Response", "Fail")
                t.printStackTrace()
            }
        })
    }

    fun postAPInfo(apInfoDTO: APInfoDTO){
        val call = apiService.postAPInfo(apInfoDTO)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Server Response", response.code().toString())
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Server Response", "Fail")
                t.printStackTrace()
            }
        })
    }

    fun patchUserData(userdatadto : UserDataDTO){
        val call = apiService.patchUserData(PreferenceUtils.getDeviceID(), userdatadto)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Server Response", response.code().toString())
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Server Response", "Fail")
                t.printStackTrace()
            }
        })
    }
}