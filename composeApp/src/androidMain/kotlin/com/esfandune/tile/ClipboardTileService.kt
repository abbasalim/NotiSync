package com.esfandune.tile

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.esfandune.activity.MainActivity
import com.esfandune.service.ClientService
import com.esfandune.setting.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClipboardTileService : TileService() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        tile.state = Tile.STATE_INACTIVE
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()

        val settings = SettingsManager(this).getSettings()
        if (settings.serverIp.isEmpty()) {
            handler.post {
                showToast("آدرس سرور تنظیم نشده است")
            }
            return
        }

        // Show loading state
        updateTileState(Tile.STATE_ACTIVE, "در حال دریافت...")

        scope.launch {
            try {
                val clientService = ClientService(settings.serverIp, settings.serverPort)
                val clipboardData = clientService.getClipboard()

                handler.post {
                    clipboardData.text?.let { clipboardText ->
                        val preview =
                            clipboardText.take(20) + if (clipboardText.length > 20) "..." else ""
                        showToast("محتوی ذخیره شد: $preview")
                    } ?: run {
                        // Open the app's main activity
                        val intent =
                            Intent(this@ClipboardTileService, MainActivity::class.java).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                putExtra("open_clipboard_screen", true)
                            }
                        startActivity(intent)
                    }
                    updateTileState(Tile.STATE_INACTIVE, "دریافت کلیپ‌بورد")
                }
            } catch (e: Exception) {
                handler.post {
                    showToast("خطا در دریافت کلیپ‌بورد: ${e.message}")
                    updateTileState(Tile.STATE_UNAVAILABLE, "خطا در اتصال")
                }
            }
        }
    }

    private fun updateTileState(state: Int, label: String? = null) {
        val tile = qsTile ?: return
        tile.state = state
        label?.let { tile.label = it }
        tile.updateTile()
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this@ClipboardTileService,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
