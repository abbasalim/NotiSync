package com.esfandune

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esfandune.setting.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class NotificationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var settingsManager: SettingsManager? = null

    fun initializeWithContext(context: Context) {
        if (settingsManager == null) {
            settingsManager = SettingsManager(context)
            checkNotificationPermission(context)
            loadSettingsFromManager()
        }
    }

    private fun loadSettingsFromManager() {
        viewModelScope.launch {
            settingsManager?.let { manager ->
                val settings = manager.getSettings()
                _uiState.value = _uiState.value.copy(
                    serverIp = settings.serverIp,
                    serverPort = settings.serverPort,
                    notificationsSent = settings.notificationsSent,
                    lastConnectionTime = settings.lastConnectionTime
                )
            }
        }
    }

    fun saveSettings(serverIp: String, serverPort: Int) {
        viewModelScope.launch {
            settingsManager?.let { manager ->
                val currentSettings = manager.getSettings()
                val newSettings = currentSettings.copy(
                    serverIp = serverIp,
                    serverPort = serverPort
                )
                manager.saveSettings(newSettings)

                _uiState.value = _uiState.value.copy(
                    serverIp = serverIp,
                    serverPort = serverPort,
                    statusMessage = "تنظیمات ذخیره شد"
                )
            } ?: run {
                _uiState.value = _uiState.value.copy(
                    statusMessage = "خطا: SettingsManager مقداردهی نشده"
                )
            }
        }
    }

    private fun checkNotificationPermission(context: Context) {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )

        val myListener = ComponentName(context, NotificationListenerService::class.java)
        val hasPermission = enabledListeners?.contains(myListener.flattenToString()) == true

        _uiState.value = _uiState.value.copy(hasNotificationPermission = hasPermission)
    }

    fun startForwardingService(context: Context) {
        if (!_uiState.value.hasNotificationPermission) {
            _uiState.value = _uiState.value.copy(
                statusMessage = "لطفاً ابتدا دسترسی نوتیفیکیشن را فعال کنید"
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isServiceRunning = true,
            statusMessage = "سرویس فوروارد شروع شد"
        )
    }

    fun stopForwardingService(context: Context) {
        _uiState.value = _uiState.value.copy(
            isServiceRunning = false,
            statusMessage = "سرویس فوروارد متوقف شد"
        )
    }

    fun refreshStats() {
        settingsManager?.let {
            loadSettingsFromManager()
        }
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }
}