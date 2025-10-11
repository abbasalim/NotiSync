package com.esfandune.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.util.Consumer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.esfandune.model.ClipboardData
import com.esfandune.screen.NotificationForwarderScreen
import com.esfandune.screen.NotificationViewModel
import com.esfandune.ui.theme.AppTheme
import com.esfandune.util.WiFiStateManager
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    private lateinit var wifiStateManager: WiFiStateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wifiStateManager = WiFiStateManager(applicationContext)

        setContent {
            val viewModel: NotificationViewModel = viewModel()

            // Handle the intent when the activity is created
            LaunchedEffect(Unit) {
                handleIntent(intent, viewModel)
            }
            // Handle new intents (in case the activity is already running)
            DisposableEffect(Unit) {
                val listener = Consumer { intent: Intent ->
                    handleIntent(intent, viewModel)
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }

            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotificationForwarderScreen()
                }
            }
        }
    }

    private fun handleIntent(intent: Intent?, viewModel: NotificationViewModel) {
        if (intent?.getBooleanExtra("open_clipboard_screen", false) == true) {
            viewModel.getClipboard(this)
        }
    }
}
