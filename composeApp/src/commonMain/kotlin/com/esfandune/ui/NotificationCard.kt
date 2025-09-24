package com.esfandune.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.esfandune.model.NotificationData
import java.util.Locale


@Composable
fun NotificationCard(
    modifier: Modifier = Modifier,
    notifications: List<NotificationData>,
    onMarkAsRead: () -> Unit
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
                notifications.forEach { notification ->
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
                    Text(
                        text = java.text.SimpleDateFormat("HH:mm", Locale.US)
                            .format(notification.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
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