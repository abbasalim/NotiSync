package com.esfandune.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esfandune.model.ClipboardData
import com.esfandune.model.UiState
import com.esfandune.service.ClientService
import com.esfandune.setting.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var settingsManager: SettingsManager? = null
    var receivingClipboard = mutableStateOf(false)
    val lastClipboardData = mutableStateOf<ClipboardData?>(null)
    val showHelDialog = mutableStateOf(false)
    val serverIp = mutableStateOf("")
    val serverPort = mutableStateOf("")
    val showAppSelectorDialog = mutableStateOf(false)
    val showServerSettings = mutableStateOf(false)
    val showQrScanner = mutableStateOf(false)

    fun initializeWithContext(context: Context) {
        if (settingsManager == null) {
            settingsManager = SettingsManager(context)
//            checkNotificationPermission(context)
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
                testConnection()
            } ?: run {
                _uiState.value = _uiState.value.copy(
                    statusMessage = "خطا: SettingsManager مقداردهی نشده"
                )
            }
        }
    }

    fun testConnection() {
        getServer()?.let { notificationService ->
            viewModelScope.launch {
                if (notificationService.testConnection()) {
                    _uiState.value = _uiState.value.copy(statusMessage = "اتصال موفقیت آمیز")
                    showServerSettings.value = false
                } else _uiState.value = _uiState.value.copy(statusMessage = "اتصال ناموفق")
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


    fun refreshStats() { // Renamed from refreshUiState to be more specific, and now reloads all settings
        settingsManager?.let {
            loadSettingsFromManager()
        }
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    fun getClipboard(context: Context) {
        if (receivingClipboard.value) {
            _uiState.value = uiState.value.copy(statusMessage = "در حال دریافت کلیپ بورد...")
            return
        }
        getServer()?.let { notificationService ->
            viewModelScope.launch {
                receivingClipboard.value = true
                val clipboardData = notificationService.getClipboard()
                receivingClipboard.value = false
                ///
                when {
                    !clipboardData.text.isNullOrEmpty() -> {
                        // Handle text content
                        val clipboardManager =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("text_content", clipboardData.text)
                        clipboardManager.setPrimaryClip(clip)
                        Log.d(
                            "clipboard",
                            "Copied text to clipboard: ${clipboardData.text.take(50)}..."
                        )
                        _uiState.value =
                            _uiState.value.copy(statusMessage = "${clipboardData.text.take(50)}...")
                    }

                    !clipboardData.imageData.isNullOrEmpty() -> {
                        lastClipboardData.value = clipboardData
                    }

                    !clipboardData.fileData.isNullOrEmpty() -> {
                        lastClipboardData.value = clipboardData
                        // Handle file content
//                        try {
//                            val fileBytes = android.util.Base64.decode(clipboardData.fileData, android.util.Base64.DEFAULT)
//                            val fileName = clipboardData.fileName ?: "clipboard_file"
//                            val mimeType = clipboardData.mimeType ?: "application/octet-stream"
//
//                            Log.d("clipboard", "Received file: $fileName (${fileBytes.size} bytes), MIME type: $mimeType")
//
//                            // In a real app, you'd want to save this to a file and share it via FileProvider
//                            // For now, we'll just log it
//                        } catch (e: Exception) {
//                            Log.e("clipboard", "Failed to process file data", e)
//                            return clipboardData.copy(error = "Failed to process file data: ${e.message}")
//                        }
                    }

                    !clipboardData.error.isNullOrEmpty() -> {
                        _uiState.value =
                            _uiState.value.copy(statusMessage = " : ${clipboardData.error}")
                        Log.d("clipboard", "Server returned error: ${clipboardData.error}")
                    }

                    else -> {
                        _uiState.value =
                            _uiState.value.copy(statusMessage = " محتوی در کلیپ برد نیست!")
                        Log.d("clipboard", "No content in clipboard")
                    }
                }
            }
        }
    }

    fun getServer(): ClientService? {
        settingsManager?.getSettings()?.let { settings ->
            return ClientService(
                serverIp = settings.serverIp,
                serverPort = settings.serverPort
            )
        }
        return null
    }

    fun showMessage(msg: String) {
        _uiState.value = _uiState.value.copy(statusMessage = msg)
    }
}
