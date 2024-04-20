package com.example.smombierecognitionalarmapplication

import android.content.Context
import android.hardware.*
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

@Deprecated
public class SensorBasedMotionProvider : MovementAnalyzer, SensorEventListener{

    private lateinit var sensorManager: SensorManager

    private var accelerationSensor : Sensor?= null
    private var magneticSensor : Sensor?= null

    private var accelerationData = FloatArray(3)
    private var magneticData = FloatArray(3)
    private var earthData = FloatArray(3)
    private var rotationMatrix = FloatArray(9)


    private var info_sb : StringBuilder = StringBuilder()

    override fun getMovementUpdates(): Flow<Location> {
        return callbackFlow {
            val sensorCallback =
                object : SensorEventCallback() {
                    override fun onSensorChanged(event: SensorEvent) {
                        super.onSensorChanged(event)
                        val location = object : Location("a"){

                        }
                        launch { send(location) }
                    }
                }
            awaitClose {
                sensorManager.unregisterListener(this@SensorBasedMotionProvider)
            }
        }

    }

    override fun initServiceManager(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.apply {
            accelerationSensor = getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magneticSensor = getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        }
    }

    override fun registerServiceListener() {
        sensorManager.apply {
            registerListener(this@SensorBasedMotionProvider, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL)
            registerListener(this@SensorBasedMotionProvider, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun unregisterServiceListener() {
        sensorManager.unregisterListener(this@SensorBasedMotionProvider)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when(event.sensor.type){
                Sensor.TYPE_ACCELEROMETER   -> getAccelerationData(event)
                Sensor.TYPE_MAGNETIC_FIELD  -> getMagneticFieldData(event)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("Accelerometer Accuracy", "changed!")
    }

    private fun getMagneticFieldData(event: SensorEvent) {
        magneticData = event.values.clone()
    }

    private fun getAccelerationData(event: SensorEvent) {
        accelerationData = event.values.clone()

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerationData, magneticData)

        //get acceleration values converted to a world coordinate system
        earthData[0] = rotationMatrix[0] * accelerationData[0] + rotationMatrix[1] * accelerationData[1] + rotationMatrix[2] * accelerationData[2]
        earthData[1] = rotationMatrix[3] * accelerationData[0] + rotationMatrix[4] * accelerationData[1] + rotationMatrix[5] * accelerationData[2]
        earthData[2] = rotationMatrix[6] * accelerationData[0] + rotationMatrix[7] * accelerationData[1] + rotationMatrix[8] * accelerationData[2]

        info_sb.append("Device Coordinate\n"+
                "x = ${accelerationData[0]},  y = ${accelerationData[1]}, z = ${accelerationData[2]}")
        Log.d("AccelerationData: ", info_sb.toString())

        info_sb = StringBuilder()
        info_sb.append("World Coordinate\n"+
                "x = ${earthData[0]},  y = ${earthData[1]}, z = ${earthData[2]}")
        Log.d("AccelerationData: ",info_sb.toString())
    }

}