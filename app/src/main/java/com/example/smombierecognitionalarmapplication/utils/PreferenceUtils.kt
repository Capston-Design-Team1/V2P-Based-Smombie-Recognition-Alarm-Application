package com.example.smombierecognitionalarmapplication.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.example.smombierecognitionalarmapplication.RetrofitManager
import com.example.smombierecognitionalarmapplication.UserModeDTO
import java.util.UUID

class PreferenceUtils(context: Context) {

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

    fun getUuid(): String {
        val uuid = getString("UUID", "")
        if (uuid == "") {
            val createdUuid = UUID.randomUUID().toString()
            setString("UUID", createdUuid)
            RetrofitManager().createUser(UserModeDTO(createdUuid, false))
            Log.d("User", "create")
            return createdUuid
        }
        return uuid
    }

    fun setUserMode(mode: Boolean){
        prefMode.edit().putBoolean("USERMOD", mode).apply()
    }

    /*
    USER MODE
        true : Pedestrian, false : Vehicle
     */
    fun getUserMode() : Boolean {
        return prefMode.getBoolean("USERMOD", false)
    }

}