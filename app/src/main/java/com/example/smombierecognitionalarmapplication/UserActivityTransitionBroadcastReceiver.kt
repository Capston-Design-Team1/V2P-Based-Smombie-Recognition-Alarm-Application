package com.example.smombierecognitionalarmapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.smombierecognitionalarmapplication.UserActivityTransitionManager.Companion.getActivityType
import com.example.smombierecognitionalarmapplication.UserActivityTransitionManager.Companion.getTransitionType

import com.google.android.gms.location.ActivityTransitionResult

class UserActivityTransitionBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val result = intent?.let { ActivityTransitionResult.extractResult(it) } ?: return
        var resultStr = ""
        for (event in result.transitionEvents) {
            resultStr += "${getActivityType(event.activityType)} " +
                    "- ${getTransitionType(event.transitionType)}"
        }
        Log.d("UserActivityReceiver", "onReceive: $resultStr")
    }

    private fun startPedestrianService(context: Context){
        Intent(context, PedestrianService::class.java).apply {
            action = PedestrianService.ACTION_START
            context.startService(this)
        }
    }

    private fun stopPedestrianService(context: Context){
        Intent(context, PedestrianService::class.java).apply {
            action = PedestrianService.ACTION_STOP
            context.startService(this)
        }
    }
}