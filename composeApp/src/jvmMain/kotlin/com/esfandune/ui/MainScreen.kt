package com.esfandune.ui


import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.esfandune.NotificationManager
import com.esfandune.model.NotificationData
import com.esfandune.ui.theme.AppTheme
import com.esfandune.util.packageToEmoji
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.awt.Desktop
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URI


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MainApp(notificationManager: NotificationManager) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedPackage by remember { mutableStateOf<String?>(null) }
    var showQRDialog by remember { mutableStateOf(false) }



    @Composable
    fun FilterApps() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            FilterChip(
                selected = selectedPackage == null,
                onClick = { selectedPackage = null },
                label = { Text("همه") },
                modifier = Modifier.padding(end = 4.dp)
            )
            notificationManager.notifications.distinctBy { it.first().packageName }.forEach { app ->
                FilterChip(
                    selected = selectedPackage == app.first().packageName,
                    onClick = { selectedPackage = app.first().packageName },
                    label = {
                        Text(
                            text = "${app.first().packageName.packageToEmoji()} ${app.first().appName}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }



    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "NotiSync",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            notificationManager.toggleSilentMode()
                        }) {
                            Icon(
                                imageVector = if (notificationManager.isSilentMode) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                                contentDescription = if (notificationManager.isSilentMode) "فعال کردن نوتیف" else "غیرفعال کردن نوتیف",
                                tint = if (notificationManager.isSilentMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        IconButton(onClick = {
                            Desktop.getDesktop().browse(URI("http://tools.esfandune.ir/"))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "درباره"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            floatingActionButton = {
                if (notificationManager.notifications.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                notificationManager.clearAll()
                                snackbarHostState.showSnackbar("تمامی اعلان‌ها پاک شدند")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = "پاک کردن همه"
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "http://${getDeviceIp()}:8080",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showQRDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "نمایش QR Code",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }


                // App filter chips
                FilterApps()

                val notifs =
                    notificationManager.notifications.filter { selectedPackage == null || it.first().packageName == selectedPackage }
                if (notifs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "هیچ نوتیفیکیشنی وجود ندارد",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {

                        }
                        items(
                            items = notifs,
                            itemContent = { notification ->
                                NotificationCard(
                                    notifications = notification,
                                    modifier = Modifier.animateItem(),
                                    onMarkAsRead = {
                                        coroutineScope.launch {
                                            sendReadConfirmation(notification)
                                            notificationManager.markAsRead(notification)
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
        
        // QR Code Dialog
        if (showQRDialog) {
            QRCodeDialog(
                url = "http://${getDeviceIp()}:8080",
                onDismiss = { showQRDialog = false }
            )
        }
    }


}



private fun getDeviceIp(): String = NetworkInterface.getNetworkInterfaces()
    .toList()
    .find { it.name == "wlan0" || it.name.startsWith("en") }?.inetAddresses
    ?.toList()
    ?.firstOrNull { !it.isLoopbackAddress && it is Inet4Address }?.hostAddress ?: "Not Found Ip"


suspend fun sendReadConfirmation(notification: List<NotificationData>) {
    // This would typically send an HTTP request back to your Android app
    // You can implement this based on your Android app's server setup
    println("Marking as read: ${notification.first().appName}")
}