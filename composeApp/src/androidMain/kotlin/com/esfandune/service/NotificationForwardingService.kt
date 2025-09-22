package com.esfandune.service


import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.esfandune.model.NotificationData
import com.esfandune.setting.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NotificationForwardingService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var notificationService: NotificationService
    private lateinit var settingsManager: SettingsManager

    companion object {
        const val CHANNEL_ID = "ForwardingServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        notificationService = NotificationService()
        settingsManager = SettingsManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("title") ?: return START_NOT_STICKY
        val message = intent.getStringExtra("message") ?: ""
        val appPackage = intent.getStringExtra("package") ?: ""
        intent.getStringExtra("package") ?: ""

        startForeground(NOTIFICATION_ID, createForegroundNotification())

        serviceScope.launch {
            try {
                val settings = settingsManager.getSettings()
                val success = notificationService.sendNotification(
                    NotificationData(
                        title = title,
                        message = message,
                        appName = AppData(packageManager).getAppName(appPackage),
//                        appIcon = AppData(packageManager).getAppIcon(appPackage)?.toBitmap(64, 64),
                        packageName = appPackage
                    ),
                    serverIp = settings.serverIp,
                    serverPort = settings.serverPort
                )

                if (success) {
                    settingsManager.incrementNotificationCount()
                    settingsManager.updateLastConnectionTime()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Notification Forwarding Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Service for forwarding notifications to desktop"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("فوروارد نوتیفیکیشن")
            .setContentText("در حال ارسال نوتیفیکیشن‌ها به دسکتاپ")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .build()
    }
}