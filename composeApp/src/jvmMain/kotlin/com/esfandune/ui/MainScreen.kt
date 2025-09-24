package com.esfandune.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.esfandune.util.packageToEmoji
import com.esfandune.NotificationManager
import com.esfandune.NotificationServer
import com.esfandune.model.NotificationData
import com.esfandune.ui.theme.AppTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.net.Inet4Address
import java.net.NetworkInterface


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MainApp() {
    val notificationManager = remember { NotificationManager() }
    val server = remember { NotificationServer(notificationManager) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedPackage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        server.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            server.stop()
        }
    }

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
            notificationManager.notifications.distinctBy { it.first().packageName } .forEach { app ->
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
                    Text(
                        text = "http://${getDeviceIp()}:8080",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }



                // App filter chips
                  FilterApps()

                val notifs = notificationManager.notifications.filter { selectedPackage==null || it.first().packageName == selectedPackage }
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
                        item{

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