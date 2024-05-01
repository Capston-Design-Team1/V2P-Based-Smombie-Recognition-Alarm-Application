package com.example.smombierecognitionalarmapplication.data.location

import android.content.Context
import android.location.Location
import kotlinx.coroutines.flow.Flow

interface MovementAnalyzer {
    fun getMovementUpdates() : Flow<Location>
    fun initServiceManager(context : Context)
    fun registerServiceListener()
    class Exceptions(message: String) : Exception()
}