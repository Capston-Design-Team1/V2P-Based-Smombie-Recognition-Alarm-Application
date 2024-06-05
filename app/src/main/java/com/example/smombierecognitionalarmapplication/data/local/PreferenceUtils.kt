package com.example.smombierecognitionalarmapplication.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import java.util.UUID

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

    private val prefToken: SharedPreferences = context.getSharedPreferences(
        "USER_TOKEN", MODE_PRIVATE
    )

    private fun getString(key: String, defVal: String): String {
        return prefUUID.getString(key, defVal).toString()
    }

    private fun setString(key: String, str: String) {
        prefUUID.edit().putString(key, str).apply()
    }

    fun createUuid() {
        var uuid = getString("UUID", "")
        if (uuid == "") {
            uuid = UUID.randomUUID().toString()
            setString("UUID", uuid)
            DEVICE_ID = uuid
            Log.d("User", "create")
        } else {
            DEVICE_ID = uuid
            Log.d("User", "already create")
        }
    }

    /*
   USER MODE
       true : Pedestrian, false : Vehicle
    */
    fun setUserMode(mode: Boolean){
        USER_MODE = mode
        prefMode.edit().putBoolean("USERMOD", mode).apply()
    }

    fun getUserMode() : Boolean {
        return prefMode.getBoolean("USERMOD", false)
    }

    fun setUserToken(token: String) {
        setToken("FCMToken", token)
    }

    fun getUserToken() : String {
        var token = getToken("FCMToken", "")
        if(token == ""){
            FirebaseMessaging.getInstance().token.addOnCompleteListener{
                task ->
                if(task.isSuccessful){
                    val newToken = task.result
                    setToken("FCMToken", newToken)
                } else {
                    Log.d("Firebase", "Failed to get token")
                }
            }
        }
        return token
    }

    private fun getToken(key: String, defVal: String): String {
        return prefToken.getString(key, defVal).toString()
    }

    private fun setToken(key: String, token: String) {
        prefToken.edit().putString(key, token).apply()
    }
}