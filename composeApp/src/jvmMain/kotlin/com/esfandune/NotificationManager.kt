package com.esfandune

import androidx.compose.runtime.mutableStateListOf
import com.esfandune.model.NotificationData
import java.awt.SystemTray
import java.awt.TrayIcon
import java.io.File
import javax.imageio.ImageIO

class NotificationManager {
    private val _notifications = mutableStateListOf<NotificationData>()
    val notifications: List<NotificationData> = _notifications
    private var trayIcon: TrayIcon? = null

    init {
        setupSystemTray()
    }

    private fun setupSystemTray() {
        if (SystemTray.isSupported()) {
            val systemTray = SystemTray.getSystemTray()

            // Create tray icon (you need to add an icon file to resources)
            val image = try {
                ImageIO.read(File("icon.png"))
            } catch (e: Exception) {
                // Create a simple colored square if no icon is found
                val bufferedImage =
                    java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB)
                val g = bufferedImage.createGraphics()
                g.color = java.awt.Color.BLUE
                g.fillRect(0, 0, 16, 16)
                g.dispose()
                bufferedImage
            }

            trayIcon = TrayIcon(image, "Notification App")
            trayIcon?.isImageAutoSize = true

            try {
                systemTray.add(trayIcon)
            } catch (e: Exception) {
                println("Could not add tray icon: ${e.message}")
            }
        }
    }

    fun addNotification(notification: NotificationData) {
        _notifications.add(0, notification) // Add to beginning
        showSystemNotification(notification)
    }

    private fun showSystemNotification(notification: NotificationData) {
        trayIcon?.displayMessage(
            notification.title,
            notification.message,
            TrayIcon.MessageType.INFO
        )
    }

    fun markAsRead(notification: NotificationData): Boolean {
        return _notifications.remove(notification)
    }
}