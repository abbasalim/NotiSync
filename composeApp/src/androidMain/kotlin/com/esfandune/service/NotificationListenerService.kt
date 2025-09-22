package com.esfandune.service

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.esfandune.setting.SettingsManager // Added import

class NotificationListenerService : NotificationListenerService() {

    private lateinit var settingsManager: SettingsManager

    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        sbn?.let { notification ->
            val packageName = notification.packageName
            val extras = notification.notification.extras

            // Skip our own notifications
            if (packageName == this.packageName) {
                Log.d("NotificationListener", "Skipping own notification: $packageName")
                return
            }

            // Check if the package is in the excluded list
            val excludedPackages = settingsManager.getExcludedPackages()
            if (packageName in excludedPackages) {
                Log.d("NotificationListener", "Skipping excluded notification: $packageName")
                return // Skip notification from excluded package
            }

            val title = extras.getCharSequence("android.title")?.toString() ?: ""
            val text = extras.getCharSequence("android.text")?.toString() ?: ""
            val appName = getAppName(packageName)

            if (title.isNotEmpty() || text.isNotEmpty()) {
                Log.d("NotificationListener", "Processing notification: $appName - $title: $text")

                // Send to forwarding service
                val intent = Intent(this, NotificationForwardingService::class.java).apply {
                    putExtra("title", if (title.isNotEmpty()) "$appName: $title" else appName)
                    putExtra("message", text)
                    putExtra("package", packageName) // Keep sending package name for potential future use
                }
                // Consider using startForegroundService if this service can run in the background for a long time
                // For immediate processing, startService is also an option, but for tasks triggered by notifications,
                // startForegroundService is often more appropriate for reliability.
                // However, NotificationForwardingService itself calls startForeground, so this might be okay.
                startService(intent) // Changed from startForegroundService to startService as NotificationForwardingService handles its own foreground state.
                                     // If NotificationForwardingService doesn't always start foreground, then startForegroundService(intent) might be better here.
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        // Handle notification removal if needed
    }

    private fun getAppName(packageName: String): String {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            Log.e("NotificationListener", "Error getting app name for $packageName", e)
            packageName
        }
    }
}
