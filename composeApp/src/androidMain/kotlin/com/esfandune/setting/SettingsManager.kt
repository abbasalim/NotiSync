package com.esfandune.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun saveSettings(settings: AppSettings) {
        prefs.edit { // Using lambda with 'edit' for conciseness
            putString("server_ip", settings.serverIp)
            putInt("server_port", settings.serverPort)
            putInt("notifications_sent", settings.notificationsSent)
            putString("last_connection", settings.lastConnectionTime)
            putStringSet("excluded_packages", settings.excludedPackages) // Save excluded packages
        }
        println("Settings saved: $settings")
    }

    fun getSettings(): AppSettings {
        return AppSettings(
            serverIp = prefs.getString("server_ip", "192.168.1.100") ?: "192.168.1.100",
            serverPort = prefs.getInt("server_port", 8080),
            notificationsSent = prefs.getInt("notifications_sent", 0),
            lastConnectionTime = prefs.getString("last_connection", "") ?: "",
            excludedPackages = prefs.getStringSet("excluded_packages", emptySet()) ?: emptySet() // Retrieve excluded packages
        )
    }

    fun saveExcludedPackages(excludedPackages: Set<String>) {
        prefs.edit {
            putStringSet("excluded_packages", excludedPackages)
        }
        println("Excluded packages saved: $excludedPackages")
    }

    fun getExcludedPackages(): Set<String> {
        return prefs.getStringSet("excluded_packages", emptySet()) ?: emptySet()
    }

    fun incrementNotificationCount() {
        val current = prefs.getInt("notifications_sent", 0)
        prefs.edit {
            putInt("notifications_sent", current + 1)
        }
    }

    fun updateLastConnectionTime() {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        prefs.edit {
            putString("last_connection", currentTime)
        }
    }
}
