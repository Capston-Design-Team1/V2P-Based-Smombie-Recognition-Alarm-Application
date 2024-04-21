package com.example.smombierecognitionalarmapplication

import android.util.Log
import com.example.smombierecognitionalarmapplication.utils.APIBASE_URL
import com.example.smombierecognitionalarmapplication.utils.PreferenceUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {
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

    fun getSmombieData(deviceId : String){
        val call = apiService.getSmombies(deviceId)
        call.enqueue(object : Callback<List<SmombiesDTO>> {
            override fun onResponse(
                call: Call<List<SmombiesDTO>>,
                response: Response<List<SmombiesDTO>>
            ) {
                val callbackList = response.body()
                Log.d("Server Response", callbackList.toString())
            }

            override fun onFailure(call: Call<List<SmombiesDTO>>, t: Throwable) {
                Log.d("Server Response", "Error")
                t.printStackTrace()
            }
        })
    }

    fun createUser(userModeDTO : UserModeDTO){
        val call = apiService.postUserMode(userModeDTO)
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

    fun patchUserData(deviceId: String, userdatadto :UserDataDTO){
        val call = apiService.patchUserData(deviceId, userdatadto)
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