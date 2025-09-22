package com.esfandune.screen

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.esfandune.screen.NotificationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationForwarderScreen() {
    val viewModel: NotificationViewModel = viewModel()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var serverIp by remember { mutableStateOf("192.168.1.100") }
    var serverPort by remember { mutableStateOf("8080") }

    LaunchedEffect(Unit) {
        viewModel.initializeWithContext(context)
    }

    LaunchedEffect(uiState.serverIp, uiState.serverPort) {
        serverIp = uiState.serverIp
        serverPort = uiState.serverPort.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "فوروارد نوتیفیکیشن به دسکتاپ",
            style = MaterialTheme.typography.headlineMedium
        )

        // Permission Status
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.hasNotificationPermission)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "وضعیت دسترسی نوتیفیکیشن:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (uiState.hasNotificationPermission) "فعال ✓" else "غیرفعال ✗",
                    color = if (uiState.hasNotificationPermission)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )

                if (!uiState.hasNotificationPermission) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تنظیمات دسترسی")
                    }
                }
            }
        }

        // Service Status
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isServiceRunning)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "وضعیت سرویس:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (uiState.isServiceRunning) "در حال اجرا ✓" else "متوقف ✗",
                    color = if (uiState.isServiceRunning)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Server Configuration
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "تنظیمات سرور",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = serverIp,
                    onValueChange = { serverIp = it },
                    label = { Text("آدرس IP سرور") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = serverPort,
                    onValueChange = { serverPort = it },
                    label = { Text("پورت سرور") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        viewModel.saveSettings(
                            serverIp = serverIp,
                            serverPort = serverPort.toIntOrNull() ?: 8080
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ذخیره تنظیمات")
                }
            }
        }

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.startForwardingService(context) },
                enabled = uiState.hasNotificationPermission && !uiState.isServiceRunning,
                modifier = Modifier.weight(1f)
            ) {
                Text("شروع فوروارد")
            }

            Button(
                onClick = { viewModel.stopForwardingService(context) },
                enabled = uiState.isServiceRunning,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("توقف فوروارد")
            }
        }

        // Statistics
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "آمار:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("نوتیفیکیشن‌های ارسال شده: ${uiState.notificationsSent}")
                Text("آخرین اتصال: ${if (uiState.lastConnectionTime.isNotEmpty()) uiState.lastConnectionTime else "هرگز"}")
            }
        }

        // Status Messages
        uiState.statusMessage?.let { message ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (message.contains("خطا"))
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.clearStatusMessage() }) {
                        Text("بستن")
                    }
                }
            }
        }
    }
}