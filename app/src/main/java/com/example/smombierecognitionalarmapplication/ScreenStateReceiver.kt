package com.example.smombierecognitionalarmapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenStateReceiver : BroadcastReceiver() {
    companion object{
        var isScreenOn : Boolean = true
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            Intent.ACTION_SCREEN_ON -> {
                isScreenOn = true
            }
            Intent.ACTION_SCREEN_OFF -> {
                isScreenOn = false
            }
        }
    }

}