package com.esfandune.service


import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        sbn?.let { notification ->
            val packageName = notification.packageName
            val extras = notification.notification.extras

            // Skip our own notifications
            if (packageName == this.packageName) return

            val title = extras.getCharSequence("android.title")?.toString() ?: ""
            val text = extras.getCharSequence("android.text")?.toString() ?: ""
            val appName = getAppName(packageName)

            if (title.isNotEmpty() || text.isNotEmpty()) {
                Log.d("NotificationListener", "New notification: $appName - $title: $text")

                // Send to forwarding service
                val intent = Intent(this, NotificationForwardingService::class.java).apply {
                    putExtra("title", if (title.isNotEmpty()) "$appName: $title" else appName)
                    putExtra("message", text)
                    putExtra("package", packageName)
                }
                startForegroundService(intent)
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
            packageName
        }
    }
}