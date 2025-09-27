package com.esfandune.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.esfandune.NotificationManager
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.net.URI


@Composable
fun MainTopBar(
    notificationManager: NotificationManager,
    showSnackbar: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    IconButton(onClick = {
        notificationManager.toggleSilentMode()
        coroutineScope.launch {
            showSnackbar(
                if (notificationManager.isSilentMode) "حالت سکوت فعال شد" else "حالت سکوت غیرفعال شد"
            )
        }
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
}
