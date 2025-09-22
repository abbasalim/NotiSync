package com.esfandune.screen

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esfandune.model.UiState
import com.esfandune.service.NotificationListenerService
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
                    lastConnectionTime = settings.lastConnectionTime,
                    excludedPackages = settings.excludedPackages // Load excluded packages
                )
            }
        }
    }

    fun saveSettings(serverIp: String, serverPort: Int) {
        viewModelScope.launch {
            settingsManager?.let { manager ->
                val currentSettings = manager.getSettings()
                // Preserve excludedPackages when saving other settings
                val newSettings = currentSettings.copy(
                    serverIp = serverIp,
                    serverPort = serverPort
                )
                manager.saveSettings(newSettings) // This now saves all fields of AppSettings

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

    // New function to save excluded packages
    fun saveExcludedPackages(packages: List<String>) {
        viewModelScope.launch {
            settingsManager?.let { manager ->
                manager.saveExcludedPackages(packages.toSet())
                _uiState.value = _uiState.value.copy(
                    excludedPackages = packages.toSet(),
                    statusMessage = "لیست برنامه‌های مستثنی ذخیره شد"
                )
            } ?: run {
                _uiState.value = _uiState.value.copy(
                    statusMessage = "خطا: SettingsManager مقداردهی نشده است"
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
        // Logic for starting the service remains the same
        _uiState.value = _uiState.value.copy(
            isServiceRunning = true,
            statusMessage = "سرویس فوروارد شروع شد"
        )
    }

    fun stopForwardingService(context: Context) {
        // Logic for stopping the service remains the same
        _uiState.value = _uiState.value.copy(
            isServiceRunning = false,
            statusMessage = "سرویس فوروارد متوقف شد"
        )
    }

    fun refreshStats() { // Renamed from refreshUiState to be more specific, and now reloads all settings
        settingsManager?.let {
            loadSettingsFromManager()
        }
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }
}
