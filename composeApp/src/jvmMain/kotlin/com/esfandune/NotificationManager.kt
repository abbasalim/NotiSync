package com.esfandune

import androidx.compose.runtime.mutableStateListOf
import com.esfandune.model.NotificationData
import com.esfandune.util.packageToEmoji
import java.awt.SystemTray
import java.awt.TrayIcon
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

            // Create tray icon
            val image = try {
                val iconUrl = javaClass.classLoader.getResource("try_icon.png")
                if (iconUrl != null) {
                    ImageIO.read(iconUrl)
                } else {
                    throw Exception("Icon not found in resources")
                }
            } catch (_: Exception) {
                // Create a simple colored square if no icon is found
                val bufferedImage =
                    java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_RGB)
                val g = bufferedImage.createGraphics()
                g.color = java.awt.Color.WHITE
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

    fun clearAll() {
        _notifications.clear()
    }

    fun addNotification(notification: NotificationData) {
        if (_notifications.isNotEmpty() && _notifications.first().packageName == notification.packageName) {
            val existingNotification = _notifications.first()
            val combinedMessage = "${existingNotification.message}\n${notification.message}"
            
            // Remove the old notification
            _notifications.removeFirst()
            
            // Add the new combined notification
            val updatedNotification = notification.copy(
                message = combinedMessage,
                timestamp = notification.timestamp
            )
            _notifications.add(0, updatedNotification)
            showSystemNotification(updatedNotification)
        } else {
            _notifications.add(0, notification)
            showSystemNotification(notification)
        }
    }

    private fun showSystemNotification(notification: NotificationData) {
        trayIcon?.displayMessage(
            "${notification.packageName.packageToEmoji()} ${notification.appName}: ${notification.title}",
            notification.message,
            TrayIcon.MessageType.INFO
        )
    }

    fun markAsRead(notification: NotificationData): Boolean {
        return _notifications.remove(notification)
    }
}