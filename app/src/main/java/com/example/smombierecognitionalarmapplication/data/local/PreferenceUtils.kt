package com.example.smombierecognitionalarmapplication.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.example.smombierecognitionalarmapplication.data.api.RetrofitManager

class PreferenceUtils(context: Context) {
    companion object{
        private lateinit var DEVICE_ID: String
        private var USER_MODE = false
        fun getDeviceID() : String {
            return DEVICE_ID
        }
        fun getUserMode() : Boolean {
            return USER_MODE
        }
    }
    private val prefUUID: SharedPreferences = context.getSharedPreferences(
        "USER_INFO", MODE_PRIVATE
    )

    private val prefMode: SharedPreferences = context.getSharedPreferences(
        "USER_MODE", MODE_PRIVATE
    )

    fun getString(key: String, defVal: String): String {
        return prefUUID.getString(key, defVal).toString()
    }

    private fun setString(key: String, str: String) {
        prefUUID.edit().putString(key, str).apply()
    }

    suspend fun createUuid() {
        val uuid = getString("UUID", "")
        if (uuid == "") {
            val createdUuid = RetrofitManager().createUser()
            setString("UUID", createdUuid)
            DEVICE_ID = createdUuid
            Log.d("User", "create")
        } else {
            DEVICE_ID = uuid
            Log.d("User", "already create")
        }
    }

    /*
   USER MODE
       true : Pedestrian, false : Vehicle
   Modify Required
    */
    fun setUserMode(mode: Boolean){
        USER_MODE = mode
        prefMode.edit().putBoolean("USERMOD", mode).apply()
    }


//    fun getUserMode() : Boolean {
//        return prefMode.getBoolean("USERMOD", false)
//    }
}