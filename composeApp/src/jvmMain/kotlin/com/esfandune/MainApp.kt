package com.esfandune

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esfandune.model.NotificationData
import com.esfandune.ui.NotificationCard
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MainApp() {
    val notificationManager = remember { NotificationManager() }
    val server = remember { NotificationServer(notificationManager) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        server.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            server.stop()
        }
    }

    MaterialTheme() {
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
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = "آدرس سرور: http://localhost:8080",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (notificationManager.notifications.isEmpty()) {
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
                        items(notificationManager.notifications) { notification ->
                            NotificationCard(
                                notification = notification,
                                onMarkAsRead = {
                                    coroutineScope.launch {
                                        sendReadConfirmation(notification)
                                        notificationManager.markAsRead(notification)
                                        snackbarHostState.showSnackbar("علامت‌گذاری به عنوان خوانده شده")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


suspend fun sendReadConfirmation(notification: NotificationData) {
    // This would typically send an HTTP request back to your Android app
    // You can implement this based on your Android app's server setup
    println("Marking as read: ${notification.title}")
}