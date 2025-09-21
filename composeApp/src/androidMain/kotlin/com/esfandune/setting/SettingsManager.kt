package com.esfandune.setting


import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import androidx.core.content.edit
import java.util.Date
import java.util.Locale


class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun saveSettings(settings: AppSettings) {
        prefs.edit().apply {
            putString("server_ip", settings.serverIp)
            putInt("server_port", settings.serverPort)
            putInt("notifications_sent", settings.notificationsSent)
            putString("last_connection", settings.lastConnectionTime)
            apply()
        }
        println("Settings saved: $settings")
    }

    fun getSettings(): AppSettings {
        return AppSettings(
            serverIp = prefs.getString("server_ip", "192.168.1.100") ?: "192.168.1.100",
            serverPort = prefs.getInt("server_port", 8080),
            notificationsSent = prefs.getInt("notifications_sent", 0),
            lastConnectionTime = prefs.getString("last_connection", "") ?: ""
        )
    }

    fun incrementNotificationCount() {
        val current = prefs.getInt("notifications_sent", 0)
        prefs.edit().putInt("notifications_sent", current + 1).apply()
    }

    fun updateLastConnectionTime() {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        prefs.edit().putString("last_connection", currentTime).apply()
    }
}