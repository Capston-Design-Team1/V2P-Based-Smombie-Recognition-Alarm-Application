package com.example.smombierecognitionalarmapplication.data.useractivitytransition

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.example.smombierecognitionalarmapplication.data.CUSTOM_REQUEST_CODE_USER_ACTION
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.tasks.await

class UserActivityTransitionManager(context: Context) {
    companion object {
        fun getActivityType(int: Int): String {
            return when (int) {
                DetectedActivity.STILL -> "STILL"
                DetectedActivity.WALKING -> "WALKING"
                DetectedActivity.RUNNING -> "RUNNING"
                DetectedActivity.TILTING -> "TILTING"
                DetectedActivity.ON_FOOT -> "ON_FOOT"
                else -> "UNKNOWN"
            }
        }

        fun getTransitionType(int: Int): String {
            return when (int) {
                0 -> "STARTED"
                1 -> "STOPPED"
                else -> ""
            }
        }

        private var isUserActivityTransitionRegistered = false
        fun isRegistered() : Boolean{
            return isUserActivityTransitionRegistered
        }
    }
    // List of activity transitions to be monitored for walking, running, and being still
    private val activityTransitions: List<ActivityTransition> by lazy {
        listOf(
            getUserActivity(DetectedActivity.STILL, ActivityTransition.ACTIVITY_TRANSITION_ENTER),
            getUserActivity(DetectedActivity.STILL, ActivityTransition.ACTIVITY_TRANSITION_EXIT),
            getUserActivity(DetectedActivity.WALKING, ActivityTransition.ACTIVITY_TRANSITION_ENTER),
            getUserActivity(DetectedActivity.WALKING, ActivityTransition.ACTIVITY_TRANSITION_EXIT),
            getUserActivity(DetectedActivity.RUNNING, ActivityTransition.ACTIVITY_TRANSITION_ENTER),
            getUserActivity(DetectedActivity.RUNNING, ActivityTransition.ACTIVITY_TRANSITION_EXIT),
            getUserActivity(DetectedActivity.TILTING, ActivityTransition.ACTIVITY_TRANSITION_ENTER),
            getUserActivity(DetectedActivity.TILTING, ActivityTransition.ACTIVITY_TRANSITION_EXIT),
            getUserActivity(DetectedActivity.ON_FOOT, ActivityTransition.ACTIVITY_TRANSITION_ENTER),
            getUserActivity(DetectedActivity.ON_FOOT, ActivityTransition.ACTIVITY_TRANSITION_EXIT)
        )
    }

    private val activityClient = ActivityRecognition.getClient(context)

    private val pendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            CUSTOM_REQUEST_CODE_USER_ACTION,
            Intent(context, UserActivityTransitionBroadcastReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getUserActivity(detectedActivity: Int, transitionType: Int): ActivityTransition {
        return ActivityTransition.Builder()
            .setActivityType(detectedActivity)
            .setActivityTransition(transitionType)
            .build()
    }

    @SuppressLint("InlinedApi")
    @RequiresPermission(ACTIVITY_RECOGNITION)
    suspend fun registerActivityTransitions() = kotlin.runCatching {
        if (!isUserActivityTransitionRegistered) {
            activityClient.requestActivityTransitionUpdates(
                ActivityTransitionRequest(activityTransitions),
                pendingIntent
            ).await()
            isUserActivityTransitionRegistered = true
        }
    }

    @SuppressLint("InlinedApi")
    @RequiresPermission(ACTIVITY_RECOGNITION)
    suspend fun deregisterActivityTransitions() = kotlin.runCatching {
        if (isUserActivityTransitionRegistered) {
            activityClient.removeActivityUpdates(pendingIntent).await()
            isUserActivityTransitionRegistered = false
        }
    }
}
