package com.esfandune.tile

import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
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
                val clipboardData = clientService.getClipboard(limitSize = 1_048_576) // 1MB limit

                handler.post {
                    val message = when {
                        clipboardData.text != null ->
                            "محتوی ذخیره شد: ${clipboardData.text.take(20).let { if (clipboardData.text.length > 20) "$it..." else it }}"
                        clipboardData.error?.contains("Content size is too large") == false -> clipboardData.error
                        else -> "برای دریافت محتوی غیرمتنی از درون برنامه اقدام نمایید."
                    }
                    showToast(message)
                }
                updateTileState(Tile.STATE_INACTIVE, "دریافت کلیپ‌بورد")

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
