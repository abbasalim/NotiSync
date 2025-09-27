package com.esfandune.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esfandune.NotificationManager
import com.esfandune.ui.NotificationCard
import com.esfandune.ui.sendReadConfirmation
import kotlinx.coroutines.launch
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
fun NotifList(
    notificationManager: NotificationManager,
    selectedPackage: String?,
    showSnackbar: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val notifs =
        notificationManager.notifications.filter { selectedPackage == null || it.first().packageName == selectedPackage }
    if (notifs.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
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
                        },
                        onCopy = { data ->
                            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                            clipboard.setContents(StringSelection(data.message), null)
                            showSnackbar("متن کپی شد")
                        }
                    )
                }
            )
        }
    }
}