package com.example.smombierecognitionalarmapplication

import android.content.Context
import android.location.Location
import kotlinx.coroutines.flow.Flow

interface MovementAnalyzer {
    public fun getMovementUpdates() : Flow<Location>
    public fun initServiceManager(context : Context)
    public fun registerServiceListener()
    public fun unregisterServiceListener()
    class Exceptions(message: String) : Exception()
}