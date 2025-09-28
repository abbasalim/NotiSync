package com.esfandune.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.esfandune.model.NotificationData
import com.esfandune.util.packageToEmoji
import java.util.Locale


@Composable
fun NotificationCard(
    modifier: Modifier = Modifier,
    notifications: List<NotificationData>,
    onMarkAsRead: () -> Unit,
    onCopy: ((NotificationData) -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
//        elevation = 4.dp
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f).padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${notifications.firstOrNull()?.packageName?.packageToEmoji()} ${notifications.firstOrNull()?.appName ?: ""}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                )
                notifications.forEach { notification ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {

                            Text(
                                text = notification.title,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                            )
                            Text(
                                text = notification.message,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = java.text.SimpleDateFormat("HH:mm", Locale.US)
                                        .format(notification.timestamp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.clickable {
                                        println("c:${notification.category} f:${notification.flags} p:${notification.progress} pm:${notification.progressMax} pi:${notification.progressIndeterminate}")
                                    }
                                )
                                onCopy?.let { copy ->
                                    if (notification.message.isNotBlank()) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy to clipboard",
                                            modifier = Modifier.clickable {
                                                copy(notification)
                                            }.size(18.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                            if (notification.progressIndeterminate) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            } else
                                if (notification.progressMax > 0) {
                                    LinearProgressIndicator(
                                        progress = { notification.progress.toFloat() / notification.progressMax.toFloat() },
                                        modifier = Modifier.fillMaxWidth(),
                                        color = ProgressIndicatorDefaults.linearColor,
                                        trackColor = ProgressIndicatorDefaults.linearTrackColor,
                                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                                    )
                                }
                        }
                    }
                }
            }
            IconButton(
                onClick = onMarkAsRead,
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "حذف اعلان",
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}