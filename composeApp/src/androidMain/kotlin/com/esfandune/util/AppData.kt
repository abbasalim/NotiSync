package com.esfandune.util

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log

class AppData(val packageManager: PackageManager) {

    fun getAppName(packageName: String): String {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            Log.e("NotificationListener", "Error getting app name for $packageName", e)
            packageName
        }
    }

    fun getAppIcon(packageName: String): Drawable? {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationIcon(applicationInfo)
        } catch (e: Exception) {
            Log.e("NotificationListener", "Error getting app name for $packageName", e)
            null
        }
    }

}