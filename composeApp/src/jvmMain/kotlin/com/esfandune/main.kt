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
import com.esfandune.ui.MainApp
import notisync.composeapp.generated.resources.Res
import notisync.composeapp.generated.resources.icon_dark
import notisync.composeapp.generated.resources.try_icon
import org.jetbrains.compose.resources.painterResource
import java.awt.SystemTray
import java.awt.TrayIcon
import kotlin.system.exitProcess


fun main() = application {

    val notificationManager = remember { NotificationManager() }
    val server = remember { NotificationServer(notificationManager) }
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
        icon = painterResource(Res.drawable.try_icon),
        menu = {
            Item(
                if (isOpen.value) "مخفی کردن" else "نمایش برنامه ",
                onClick = { isOpen.value = isOpen.value.not() }
            )

            Item(
                "خروج کامل",
                onClick = { exitProcess(1) }
            )
        }
    )

    if (isOpen.value) {
        Window(
            icon = painterResource(Res.drawable.icon_dark),
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