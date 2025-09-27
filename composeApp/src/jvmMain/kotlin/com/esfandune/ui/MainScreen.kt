package com.esfandune.ui


import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.esfandune.NotificationManager
import com.esfandune.model.NotificationData
import com.esfandune.ui.component.ConnectCardInfo
import com.esfandune.ui.component.NotifList
import com.esfandune.ui.theme.AppTheme
import com.esfandune.util.packageToEmoji
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.awt.Desktop
import java.net.URI


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MainApp(notificationManager: NotificationManager) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedPackage by remember { mutableStateOf<String?>(null) }


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
                ConnectCardInfo()
                FilterApps()
                NotifList(
                    notificationManager,
                    selectedPackage,
                    showSnackbar = { coroutineScope.launch { snackbarHostState.showSnackbar(it) } })
            }
        }


    }


}


suspend fun sendReadConfirmation(notification: List<NotificationData>) {
    // This would typically send an HTTP request back to your Android app
    // You can implement this based on your Android app's server setup
    println("Marking as read: ${notification.first().appName}")
}