package com.example.smombierecognitionalarmapplication.utils

import android.app.ActivityManager
import android.content.Context

fun checkSystemMemoryUsage(context: Context): String {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val availableMemory = memoryInfo.availMem / (1024 * 1024)
    val totalMemory = memoryInfo.totalMem / (1024 * 1024)

    val usedMemoryPercentage = ((totalMemory - availableMemory).toFloat() / totalMemory) * 100
    return "Available Memory : ${availableMemory}MB\nTotal Memory : ${totalMemory}MB"
}

fun checkMemoryUsage(context: Context): String {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val pid = android.os.Process.myPid()
    val info = activityManager.getProcessMemoryInfo(intArrayOf(pid))[0]

    return "Total Private Dirty Memory: ${info.totalPrivateDirty} KB\n" +
            "Total Pss: ${info.totalPss} KB\n" +
            "Total Shared Dirty Memory: ${info.totalSharedDirty} KB"
}

fun checkMemoryUsageHigh(context: Context): Boolean {
    val MEMEORY_THRESHOLD = 30
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val availableMemory = memoryInfo.availMem / (1024 * 1024)
    val totalMemory = memoryInfo.totalMem / (1024 * 1024)

    val usedMemoryPercentage = ((totalMemory - availableMemory).toFloat() / totalMemory) * 100
    return usedMemoryPercentage > MEMEORY_THRESHOLD
}