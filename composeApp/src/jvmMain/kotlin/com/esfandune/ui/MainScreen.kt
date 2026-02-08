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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.esfandune.ui.AppLanguage
import com.esfandune.ui.LocalAppStrings
import com.esfandune.ui.component.ConnectCardInfo
import com.esfandune.ui.component.MainTopBar
import com.esfandune.ui.component.NotifList
import com.esfandune.ui.theme.AppTheme
import com.esfandune.util.packageToEmoji
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MainApp(
    notificationManager: NotificationManager,
    port: Int,
    language: AppLanguage,
    onToggleLanguage: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedPackage by remember { mutableStateOf<String?>(null) }
    val strings = LocalAppStrings.current


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
                label = { Text(strings.filterAll) },
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
                            text = strings.appName,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    actions = {
                        MainTopBar(
                            notificationManager,
                            language = language,
                            onToggleLanguage = onToggleLanguage,
                            showSnackbar = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(it)
                                }
                            })
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
                                snackbarHostState.showSnackbar(strings.notificationsCleared)
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = strings.clearAllContentDescription
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
                ConnectCardInfo(port = port)
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
