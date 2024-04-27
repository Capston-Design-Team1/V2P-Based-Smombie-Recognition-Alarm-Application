package com.example.smombierecognitionalarmapplication.data.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.smombierecognitionalarmapplication.common.utils.HTTPResponseCheck
import com.example.smombierecognitionalarmapplication.data.APIBASE_URL
import com.example.smombierecognitionalarmapplication.data.SMOMBIE_TRIGGER_EXPIRATION_TIME
import com.example.smombierecognitionalarmapplication.data.api.models.APInfoDTO
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
import java.util.concurrent.ConcurrentHashMap

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
        private val _smombieUpdate = MutableSharedFlow<List<SmombiesDTO>?>()
        val smombieUpdate = _smombieUpdate.asSharedFlow()
        private val _alarm = MutableSharedFlow<Boolean>()
        val alarm = _alarm.asSharedFlow()
        private lateinit var triggeredSmombieIdMap : ConcurrentHashMap<String, Long>
        private val handler = Handler(Looper.getMainLooper())
    }

    suspend fun getSmombieData() : Boolean {
    /*
    특정 사용자에게서 100초 이내로 다시 알림 받을 수 없도록 만들기.
    */
        var alert = false
        val call = apiService.getSmombies(PreferenceUtils.getDeviceID())
        call.awaitResponse().runCatching {
            if(HTTPResponseCheck(code())){
                val responselist = body() ?: return false
                _smombieUpdate.emit(responselist)
                responselist[0].smombieLocationListDTO.forEach{
                    smombieInfo ->
                    withContext(Dispatchers.Default){
                        val key = smombieInfo.deviceId
                        if(!triggeredSmombieIdMap.containsKey(key)){
                            triggeredSmombieIdMap.put(key, SMOMBIE_TRIGGER_EXPIRATION_TIME)
                            handler.postDelayed({
                                triggeredSmombieIdMap.remove(key)
                            }, SMOMBIE_TRIGGER_EXPIRATION_TIME)
                            alert = true
                        }
                    }
                }

                if(alert){
                    _alarm.emit(alert)
                }
                return alert
            }
        }.onFailure {
            exception -> exception.printStackTrace()
        }
        return false
    }

    fun postUserMode(){
        val call = apiService.postUserCreation(UserModeDTO(PreferenceUtils.getDeviceID(),PreferenceUtils.getUserMode()))
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