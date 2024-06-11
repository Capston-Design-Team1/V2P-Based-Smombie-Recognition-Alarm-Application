package com.example.smombierecognitionalarmapplication.data.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.smombierecognitionalarmapplication.common.utils.HTTPResponseCheck
import com.example.smombierecognitionalarmapplication.data.APIBASE_URL
import com.example.smombierecognitionalarmapplication.data.SMOMBIE_TRIGGER_EXPIRATION_TIME
import com.example.smombierecognitionalarmapplication.data.api.models.APInfoDTO
import com.example.smombierecognitionalarmapplication.data.api.models.FcmTokenDTO
import com.example.smombierecognitionalarmapplication.data.api.models.SmombiesDTO
import com.example.smombierecognitionalarmapplication.data.api.models.UserDataDTO
import com.example.smombierecognitionalarmapplication.data.api.models.UserModeDTO
import com.example.smombierecognitionalarmapplication.data.local.PreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap

object RetrofitManager {

    private val retrofit by lazy {
        Retrofit.Builder().apply {
            baseUrl(APIBASE_URL)
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }
    private val apiService: APIService by lazy {
        retrofit.create(APIService::class.java)
    }
    private val _smombieUpdate = MutableSharedFlow<SmombiesDTO?>()
    val smombieUpdate = _smombieUpdate.asSharedFlow()
    private val _alarm = MutableSharedFlow<Boolean>()
    val alarm = _alarm.asSharedFlow()
    private val triggeredSmombieIdMap: ConcurrentHashMap<String, Long> = ConcurrentHashMap()
    private val handler = Handler(Looper.getMainLooper())
    var connection = true;

    suspend fun getSmombieData(): Boolean {
        var alert = false
        val call = apiService.getSmombies(PreferenceUtils.getDeviceID())

        call.awaitResponse().runCatching {
            if (HTTPResponseCheck(code())) {
                val response = body() ?: return false
                Log.d("SmombieResponse", "${response.riskLevel1.firstOrNull()} , ${response.riskLevel2.firstOrNull()}, ${response.riskLevel3.firstOrNull()}")
                _smombieUpdate.emit(response)
                response.riskLevel1?.forEach { smombieInfo ->
                    withContext(Dispatchers.Default) {
                        val key = smombieInfo.deviceId
                        if (!triggeredSmombieIdMap.containsKey(key)) {
                            triggeredSmombieIdMap[key] = SMOMBIE_TRIGGER_EXPIRATION_TIME
                            handler.postDelayed({
                                triggeredSmombieIdMap.remove(key)
                            }, SMOMBIE_TRIGGER_EXPIRATION_TIME)
                            alert = true
                        }
                    }
                }

                if (alert) {
                    _alarm.emit(alert)
                }
                return alert
            }
        }.onFailure { exception ->
            exception.printStackTrace()
            if(exception is IOException || exception is SocketTimeoutException){
                connection = false
            }
        }
        return false
    }

    fun postUser() {
        val call = apiService.postUserCreation(UserModeDTO(PreferenceUtils.getDeviceID(), PreferenceUtils.getUserMode()))
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Server Response postUserMode", response.code().toString())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Server Response", "Fail postUserMode")
                t.printStackTrace()
                if(t is SocketTimeoutException){
                    connection = false
                }
            }
        })
    }

    fun postAPInfo(apInfoDTO: APInfoDTO) {
        val call = apiService.postAPInfo(apInfoDTO)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Server Response postAPInfo", response.code().toString())
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Server Response", "Fail postAPInfo")
                t.printStackTrace()
                if(t is SocketTimeoutException){
                    connection = false
                }
            }
        })
    }

    fun patchUserData(userdatadto: UserDataDTO) {
        val call = apiService.patchUserData(PreferenceUtils.getDeviceID(), userdatadto)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Server Response patchUserData", response.code().toString())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Server Response", "Fail patchUserData")
                t.printStackTrace()
                if(t is SocketTimeoutException){
                    connection = false
                }
            }
        })
    }

    fun postUserToken(fcmTokenDTO: FcmTokenDTO) {
        val call = apiService.postUserToken(fcmTokenDTO)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Server Response postUserToken", response.code().toString())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("Server Response", "Fail postUserToken")
                t.printStackTrace()
                if(t is SocketTimeoutException){
                    connection = false
                }
            }
        })
    }
}
