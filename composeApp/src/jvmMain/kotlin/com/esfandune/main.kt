package com.esfandune

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.esfandune.model.NotificationData
import com.esfandune.ui.MainApp
import notisync.composeapp.generated.resources.Res
import notisync.composeapp.generated.resources.icon_dark
import org.jetbrains.compose.resources.painterResource
import java.awt.SystemTray
import java.awt.TrayIcon
import kotlin.system.exitProcess



fun main() = application {

    val notificationManager = remember { NotificationManager() }
    val server = remember { NotificationServer(notificationManager) }
    val icon = painterResource(Res.drawable.icon_dark)
    val isOpen = remember { mutableStateOf(true) }

    // Set up system notification callback
    LaunchedEffect(notificationManager) {
        notificationManager.onShowSystemNotification = { title, message ->
            if (SystemTray.isSupported()) {
                try {
                    val systemTray = SystemTray.getSystemTray()
                    val trayIcons = systemTray.trayIcons
                    if (trayIcons.isNotEmpty()) {
                        trayIcons[0].displayMessage(title, message, TrayIcon.MessageType.INFO)
                    }
                } catch (e: Exception) {
                    println("Failed to show system notification: ${e.message}")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        server.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            server.stop()
        }
    }

    Tray(
        icon = icon,
        menu = {
            Item(
                "Show",
                onClick = { isOpen.value = true }
            )

            // Only show test notification in debug mode
            if (isDebugMode()) {
                Item(
                    "Test Notification",
                    onClick = {
                        notificationManager.addNotification(
                            NotificationData(
                                packageName = "com.test.app",
                                appName = "Test App",
                                title = "Test Notification",
                                message = "This is a test notification from tray menu",
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                )
            }
            
            Item(
                "Exit",
                onClick = { exitProcess(1) }
            )
        }
    )

    if (isOpen.value) {
        Window(
            icon = icon,
            onCloseRequest = { isOpen.value = false },
            title = "NotiSync",
            state = WindowState(
//            position = WindowPosition(100.dp, 100.dp),
                size = DpSize(500.dp, 800.dp)
            )
        ) {
            MainApp(notificationManager)
        }
    }
}