package com.esfandune.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esfandune.model.ClipboardData
import com.esfandune.model.UiState
import com.esfandune.service.NotificationListenerService
import com.esfandune.service.NotificationService
import com.esfandune.setting.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NotificationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var settingsManager: SettingsManager? = null
    private var clipboardData = mutableStateOf<ClipboardData?>(null)

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


    fun refreshStats() { // Renamed from refreshUiState to be more specific, and now reloads all settings
        settingsManager?.let {
            loadSettingsFromManager()
        }
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    fun getClipboard(context: Context) {
        settingsManager?.getSettings()?.let { settings ->
            val notificationService =
                NotificationService(serverIp = settings.serverIp, serverPort = settings.serverPort)
            viewModelScope.launch {
                val clipboardData = notificationService.getClipboard()
                ///
                when {
                    !clipboardData.text.isNullOrEmpty() -> {
                        // Handle text content
                        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("text_content", clipboardData.text)
                        clipboardManager.setPrimaryClip(clip)
                        Log.d("clipboard", "Copied text to clipboard: ${clipboardData.text.take(50)}...")
                    }
                    !clipboardData.imageData.isNullOrEmpty() -> {
                        // Handle image content
                        try {
                            val imageBytes = android.util.Base64.decode(clipboardData.imageData, android.util.Base64.DEFAULT)
                            val clip = ClipData.newUri(
                                context.contentResolver,
                                "image_content",
                                "content://com.esfandune.notisync/clipboard_image.png".toUri()
                            )

                            // Save image to cache and share it
                            val context = context.applicationContext
                            val cacheDir = File(context.cacheDir, "shared_images")
                            if (!cacheDir.exists()) {
                                cacheDir.mkdirs()
                            }

                            val file = File(cacheDir, "shared_image_${System.currentTimeMillis()}.png")
                            FileOutputStream(file).use { output ->
                                output.write(imageBytes)
                            }

                            // Create a content URI using FileProvider
                            val contentUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )

                            // Create share intent
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, contentUri)
                                type = clipboardData.mimeType ?: "image/png"
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }

                            // Start the share activity
                            val shareIntentChooser = Intent.createChooser(
                                shareIntent,
                                "اشتراک‌گذاری تصویر دریافتی"
                            )
                            shareIntentChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(shareIntentChooser)

                            Log.d("clipboard", "Image shared successfully")
                        } catch (e: Exception) {
                            Log.e("clipboard", "Failed to process image data", e)
                            return clipboardData.copy(error = "Failed to process image data: ${e.message}")
                        }
                    }
                    !clipboardData.fileData.isNullOrEmpty() -> {
                        // Handle file content
                        try {
                            val fileBytes = android.util.Base64.decode(clipboardData.fileData, android.util.Base64.DEFAULT)
                            val fileName = clipboardData.fileName ?: "clipboard_file"
                            val mimeType = clipboardData.mimeType ?: "application/octet-stream"

                            Log.d("clipboard", "Received file: $fileName (${fileBytes.size} bytes), MIME type: $mimeType")

                            // In a real app, you'd want to save this to a file and share it via FileProvider
                            // For now, we'll just log it
                        } catch (e: Exception) {
                            Log.e("clipboard", "Failed to process file data", e)
                            return clipboardData.copy(error = "Failed to process file data: ${e.message}")
                        }
                    }
                    !clipboardData.error.isNullOrEmpty() -> {
                        Log.d("clipboard", "Server returned error: ${clipboardData.error}")
                    }
                    else -> {
                        Log.d("clipboard", "No content in clipboard")
                    }
                }





                ///
                _uiState.value = _uiState.value.copy(
                    statusMessage = clipboardData.text ?:clipboardData.fileName ?: clipboardData.error ?: "unknown error"
                )
            }
        }
    }
}
