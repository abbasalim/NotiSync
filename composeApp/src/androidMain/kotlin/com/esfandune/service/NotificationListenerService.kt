package com.esfandune.service

import android.content.Intent
import android.graphics.drawable.Drawable
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
            if (title.isNotEmpty() || text.isNotEmpty()) {
                Log.d("NotificationListener", "Processing notification: $packageName - $title: $text")

                // Send to forwarding service
                val intent = Intent(this, NotificationForwardingService::class.java).apply {
                    putExtra("title", title)
                    putExtra("message", text)
                    putExtra("package", packageName) // Keep sending package name for potential future use
                }

                startService(intent)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        // Handle notification removal if needed
    }


}
