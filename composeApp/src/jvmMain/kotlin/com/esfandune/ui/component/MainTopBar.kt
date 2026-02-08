package com.esfandune.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.esfandune.NotificationManager
import com.esfandune.ui.HelpDialog
import com.esfandune.ui.LocalAppStrings
import com.esfandune.ui.AppLanguage
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.net.URI


@Composable
fun MainTopBar(
    notificationManager: NotificationManager,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    showSnackbar: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var showHelpDialog by remember { mutableStateOf(false) }
    val strings = LocalAppStrings.current
    if (showHelpDialog){
        HelpDialog(onDismiss = { showHelpDialog = false })
    }
    IconButton(onClick = onToggleLanguage) {
        Text(if (language == AppLanguage.FA) strings.english else strings.persian)
    }
    IconButton(onClick = {
        notificationManager.toggleSilentMode()
        coroutineScope.launch {
            showSnackbar(
                if (notificationManager.isSilentMode) strings.silentEnabled else strings.silentDisabled
            )
        }
    }) {
        Icon(
            imageVector = if (notificationManager.isSilentMode) Icons.Default.NotificationsOff else Icons.Default.Notifications,
            contentDescription = if (notificationManager.isSilentMode) strings.enableNotifications else strings.disableNotifications,
            tint = if (notificationManager.isSilentMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
    IconButton(onClick = {
        showHelpDialog = true
    }) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Help,
            contentDescription = strings.help
        )
    }

    IconButton(onClick = {
        Desktop.getDesktop().browse(URI("http://tools.esfandune.ir/"))
    }) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = strings.about
        )
    }

}
