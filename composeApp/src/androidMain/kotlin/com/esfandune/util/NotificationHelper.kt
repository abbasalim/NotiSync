package com.esfandune.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.esfandune.R
import com.esfandune.activity.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "notisync_channel"
    private const val NOTIFICATION_ID = 1001

    fun showClipboardNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "اطلاع‌رسانی‌های NotiSync",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "نمایش اطلاع‌رسانی‌های برنامه"
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create an intent to open the app when notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismiss when clicked
            .setContentIntent(pendingIntent) // Open app when clicked
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
