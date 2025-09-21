package com.esfandune

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.esfandune.model.NotificationData
import com.esfandune.ui.NotificationCard
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun MainApp() {
    val notificationManager = remember { NotificationManager() }
    val server = remember { NotificationServer(notificationManager) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        server.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            server.stop()
        }
    }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "اپ نوتیفیکیشن",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "آدرس سرور: http://localhost:8080",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (notificationManager.notifications.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
//                    elevation = 2.dp
                ) {
                    Text(
                        text = "هیچ نوتیفیکیشنی وجود ندارد",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn {
                    items(notificationManager.notifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onMarkAsRead = {
                                coroutineScope.launch {
                                    // Send read confirmation to Android app
                                    sendReadConfirmation(notification)
                                    notificationManager.markAsRead(notification)
                                }
                            }
                        )
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