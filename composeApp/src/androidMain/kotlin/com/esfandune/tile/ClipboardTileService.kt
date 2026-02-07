package com.esfandune.tile

import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.esfandune.R
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
        if (settings.servers.isEmpty()) {
            handler.post {
                showToast(getString(R.string.server_address_not_set))
            }
            return
        }

        // Show loading state
        updateTileState(Tile.STATE_ACTIVE, getString(R.string.tile_receiving))

        scope.launch {
            try {
                val clientService = ClientService(settings.servers)
                val clipboardData = clientService.getClipboard(limitSize = 1_048_576) // 1MB limit

                handler.post {
                    val message = when {
                        clipboardData.text != null -> {
                            val preview = clipboardData.text.take(20).let {
                                if (clipboardData.text.length > 20) "$it..." else it
                            }
                            getString(R.string.tile_content_saved, preview)
                        }
                        clipboardData.error?.contains("Content size is too large") == false -> {
                            when (clipboardData.error) {
                                ClientService.ERROR_NO_SERVER_CONFIGURED ->
                                    getString(R.string.error_no_server_configured)
                                ClientService.ERROR_CLIPBOARD_FETCH_FAILED ->
                                    getString(R.string.error_clipboard_fetch_failed)
                                else -> clipboardData.error ?: ""
                            }
                        }
                        else -> getString(R.string.tile_non_text_hint)
                    }
                    showToast(message)
                }
                updateTileState(Tile.STATE_INACTIVE, getString(R.string.clipboard_tile_label))

            } catch (e: Exception) {
                handler.post {
                    showToast(getString(R.string.tile_receive_error, e.message ?: ""))
                    updateTileState(Tile.STATE_UNAVAILABLE, getString(R.string.tile_connection_error))
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
