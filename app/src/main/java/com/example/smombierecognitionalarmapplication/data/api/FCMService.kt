package com.example.smombierecognitionalarmapplication.data.api

import android.util.Log
import com.example.smombierecognitionalarmapplication.data.api.models.FcmTokenDTO
import com.example.smombierecognitionalarmapplication.data.local.PreferenceUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val prefUtil = PreferenceUtils(applicationContext)
        prefUtil.setUserToken(token)
        RetrofitManager.postUserToken(FcmTokenDTO(PreferenceUtils.getDeviceID(), token))
        Log.d("FCM Token", "Create New")
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}