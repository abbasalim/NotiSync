package com.esfandune.service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.esfandune.R
import com.esfandune.model.NotificationCategory
import com.esfandune.model.NotificationData
import com.esfandune.setting.SettingsManager
import com.esfandune.util.AppData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**2
 * This service is responsible for forwarding notifications to the desktop application.
 * It listens to incoming notifications from the system and sends them to the desktop application.
 */
class NotificationForwardingService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var clientService: ClientService
    private lateinit var settingsManager: SettingsManager

    companion object {
        const val CHANNEL_ID = "ForwardingServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val settings = settingsManager.getSettings()
        ///چون ممکنه کاربر در تنظیمات اپدیت کنه و این بروز نمیشه
        clientService =
            ClientService(serverIp = settings.serverIp, serverPort = settings.serverPort)
        ///
        if (intent == null) return START_NOT_STICKY
        val packageName = intent.getStringExtra("packageName") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val flags = intent.getIntExtra("flags", 0)
        val title = intent.getStringExtra("title") ?: ""
        val message = intent.getStringExtra("message") ?: ""
        val progress = intent.getIntExtra("progress", 0)
        val progressMax = intent.getIntExtra("progressMax", 0)
        val progressIndeterminate = intent.getBooleanExtra("progressIndeterminate", false)
        Log.d("NotificationListener", "Processing notification: $packageName - $title: $message")

        startForeground(NOTIFICATION_ID, createForegroundNotification())
        serviceScope.launch {
            try {

                val success = clientService.sendNotification(
                    NotificationData(
                        title = title,
                        message = message,
                        appName = AppData(packageManager).getAppName(packageName),
//                        appIcon = AppData(packageManager).getAppIcon(appPackage)?.toBitmap(64, 64),
                        category = NotificationCategory.entries.firstOrNull { it.value == category } ,
                        flags = flags,
                        progress = progress,
                        progressMax = progressMax,
                        progressIndeterminate = progressIndeterminate,
                        packageName = packageName
                    ),

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



    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("فوروارد نوتیفیکیشن")
            .setContentText("در حال ارسال نوتیفیکیشن‌ها به دسکتاپ")
            .setSmallIcon(R.drawable.ic_launcher_monochrome)
            .build()
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
}