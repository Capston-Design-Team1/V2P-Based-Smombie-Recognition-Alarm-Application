package com.example.smombierecognitionalarmapplication.common.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenStateReceiver : BroadcastReceiver() {
    companion object{
        private var screenOn : Boolean = true
        fun isScreenOn() : Boolean{
            return screenOn
        }
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            Intent.ACTION_SCREEN_ON -> {
                screenOn = true
            }
            Intent.ACTION_SCREEN_OFF -> {
                screenOn = false
            }
        }
    }

}