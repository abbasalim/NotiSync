package com.esfandune.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.esfandune.screen.NotificationForwarderScreen
import com.esfandune.util.WiFiStateManager

class MainActivity : ComponentActivity() {
    private lateinit var wifiStateManager: WiFiStateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wifiStateManager = WiFiStateManager(applicationContext)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotificationForwarderScreen()
                }
            }
        }
    }
}
