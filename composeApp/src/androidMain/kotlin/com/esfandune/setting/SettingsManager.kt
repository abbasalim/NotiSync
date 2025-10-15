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
        prefs.edit {
            putStringSet("servers", settings.servers)
            putInt("notifications_sent", settings.notificationsSent)
            putString("last_connection", settings.lastConnectionTime)
            putStringSet("excluded_packages", settings.excludedPackages)

        }
        println("Settings saved: $settings")
    }

    fun getSettings(): AppSettings {

        val settings = AppSettings(
            servers = prefs.getStringSet("servers", emptySet()) ?: emptySet(),
            notificationsSent = prefs.getInt("notifications_sent", 0),
            lastConnectionTime = prefs.getString("last_connection", "") ?: "",
            excludedPackages = prefs.getStringSet("excluded_packages", emptySet()) ?: emptySet()
        )
        println("Settings saved: $settings")
        return settings
    }

    fun saveExcludedPackages(excludedPackages: Set<String>) {
        prefs.edit {
            putStringSet("excluded_packages", excludedPackages)
        }
        println("Excluded packages saved: $excludedPackages")
    }

    fun getExcludedPackages(): Set<String> {
        val pkgs = prefs.getStringSet("excluded_packages", emptySet()) ?: emptySet()
        return  pkgs.plus("com.android.systemui")
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
