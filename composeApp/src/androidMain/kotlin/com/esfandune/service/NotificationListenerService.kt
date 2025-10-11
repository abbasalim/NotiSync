package com.esfandune.service

import android.app.Notification.EXTRA_PROGRESS
import android.app.Notification.EXTRA_PROGRESS_INDETERMINATE
import android.app.Notification.EXTRA_PROGRESS_MAX
import android.app.Notification.EXTRA_TEXT
import android.app.Notification.EXTRA_TITLE
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.esfandune.setting.SettingsManager
import com.esfandune.util.WiFiStateManager
import kotlin.text.category
import kotlin.toString

class NotificationListenerService : NotificationListenerService() {
    /**
     * This service listens to system notifications only when connected to WiFi.
     * When a new notification is posted, this service receives it,
     * extracts its information such as title, text, and package name,
     * and then sends this information to the `NotificationForwardingService` for further processing.
     * The service automatically disables itself when WiFi is disconnected.
     */
    private lateinit var settingsManager: SettingsManager
    private lateinit var wifiStateManager: WiFiStateManager

    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(this)
        wifiStateManager = WiFiStateManager(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if (wifiStateManager.isWifiConnected.value == false) {
            Log.d("NotificationListener", "WiFi is not connected, skipping notification")
            return
        }
        sbn?.let { notification ->
            val packageName = notification.packageName
            if (packageName == this.packageName) {
                Log.d("NotificationListener", "Skipping own notification: $packageName")
                return
            }
            val excludedPackages = settingsManager.getExcludedPackages()
            if (packageName in excludedPackages) {
                Log.d("NotificationListener", "Skipping excluded notification: $packageName")
                return
            }

            val category = notification.notification.category
            val flags = notification.notification.flags
            val extras = notification.notification.extras
            val title = extras.getCharSequence(EXTRA_TITLE)?.toString() ?: ""
            val message = extras.getCharSequence(EXTRA_TEXT)?.toString() ?: ""
            val progress = extras.getInt(EXTRA_PROGRESS, 0)
            val progressMax = extras.getInt(EXTRA_PROGRESS_MAX, 0)
            val progressIndeterminate = extras.getBoolean(EXTRA_PROGRESS_INDETERMINATE, false)


            if (title.isBlank() || message.isBlank())
                return

            ///نمیشه مستقیم نوتیف رو ارسال کرد برخی اپها مثل پیامک خطای امنتی میده
            val intent = Intent(this, NotificationForwardingService::class.java).apply {
                putExtra("packageName", packageName)
                putExtra("category", category)
                putExtra("flags", flags)
                putExtra("title", title)
                putExtra("message", message)
                putExtra("progress", progress)
                putExtra("progressMax", progressMax)
                putExtra("progressIndeterminate", progressIndeterminate)
            }
            startService(intent)
        }
    }


}
