package com.esfandune.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esfandune.R
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
    private var appContext: Context? = null
    var receivingClipboard = mutableStateOf(false)
    val lastClipboardData = mutableStateOf<ClipboardData?>(null)
    val showHelDialog = mutableStateOf(false)
    val newServerIp = mutableStateOf("")
    val newServerPort = mutableStateOf("")

    val showAppSelectorDialog = mutableStateOf(false)
    val showServerSettings = mutableStateOf(false)
    val showQrScanner = mutableStateOf(false)

    fun initializeWithContext(context: Context) {
        if (settingsManager == null) {
            appContext = context.applicationContext
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
                    serverAddress = settings.servers.toList(),
                    notificationsSent = settings.notificationsSent,
                    lastConnectionTime = settings.lastConnectionTime,
                    excludedPackages = settings.excludedPackages
                )
            }
        }
    }
//testForAddressIndex >>> if want test an new connection, must not null
    fun saveSettings(addressList: List<String>, testForAddressIndex: Int? = null) {
        viewModelScope.launch {
            settingsManager?.let { manager ->
                val currentSettings = manager.getSettings()
                val newSettings = currentSettings.copy(
                    servers = addressList.toSet(),
                )
                manager.saveSettings(newSettings)

                _uiState.value = _uiState.value.copy(
                    statusMessage = getString(R.string.status_settings_saved),
                    serverAddress = addressList
                )
                testForAddressIndex?.let {
                    testConnection(addressList.getOrNull(it))
                }
            } ?: run {
                _uiState.value = _uiState.value.copy(
                    statusMessage = getString(R.string.error_settings_manager_uninitialized)
                )
            }
        }
    }

    fun testConnection(server: String?) {
        getServer()?.let { notificationService ->
            viewModelScope.launch {
                if (notificationService.testConnection(server).count { it.second } > 0) {
                    _uiState.value = _uiState.value.copy(statusMessage = getString(R.string.connection_successful))
                    showServerSettings.value = false
                } else _uiState.value = _uiState.value.copy(statusMessage = getString(R.string.connection_failed))
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
                    statusMessage = getString(R.string.excluded_apps_saved)
                )
            } ?: run {
                _uiState.value = _uiState.value.copy(
                    statusMessage = getString(R.string.error_settings_manager_uninitialized)
                )
            }
        }
    }


    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    fun getClipboard(context: Context) {
        if (receivingClipboard.value) {
            _uiState.value = uiState.value.copy(statusMessage = context.getString(R.string.receiving_clipboard))
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
                        val localizedError = when (clipboardData.error) {
                            ClientService.ERROR_NO_SERVER_CONFIGURED ->
                                context.getString(R.string.error_no_server_configured)
                            ClientService.ERROR_CLIPBOARD_FETCH_FAILED ->
                                context.getString(R.string.error_clipboard_fetch_failed)
                            else -> clipboardData.error
                        }
                        _uiState.value =
                            _uiState.value.copy(
                                statusMessage = context.getString(
                                    R.string.clipboard_error_prefix,
                                    localizedError
                                )
                            )
                        Log.d("clipboard", "Server returned error: ${clipboardData.error}")
                    }

                    else -> {
                        _uiState.value =
                            _uiState.value.copy(statusMessage = context.getString(R.string.clipboard_empty))
                        Log.d("clipboard", "No content in clipboard")
                    }
                }
            }
        }
    }

    fun getServer(): ClientService? {
        settingsManager?.getSettings()?.let { settings ->
            return ClientService(settings.servers)
        }
        return null
    }

    fun showMessage(msg: String) {
        _uiState.value = _uiState.value.copy(statusMessage = msg)
    }

    private fun getString(resId: Int, vararg formatArgs: Any): String {
        return appContext?.getString(resId, *formatArgs) ?: ""
    }
}
