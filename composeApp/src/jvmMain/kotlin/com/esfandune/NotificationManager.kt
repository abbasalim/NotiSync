package com.esfandune

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.esfandune.model.NotificationData
import com.esfandune.util.packageToEmoji

class NotificationManager {
    private val _notifications = mutableStateListOf<List<NotificationData>>()
    val notifications: List<List<NotificationData>> = _notifications

    // Silent mode state
    private var _isSilentMode by mutableStateOf(false)
    val isSilentMode: Boolean get() = _isSilentMode

    // Callback for showing system notifications
    var onShowSystemNotification: ((String, String) -> Unit)? = null

    fun clearAll() {
        _notifications.clear()
    }

    fun toggleSilentMode() {
        _isSilentMode = !_isSilentMode
    }

    fun addNotification(notification: NotificationData) {
        if (_notifications.isNotEmpty() && _notifications.first()
                .first().packageName == notification.packageName
        ) {
            val existingNotification = _notifications.first()
            val addedList = existingNotification.toMutableList()
            val perNotifApp = addedList.getOrNull(0)
            //if is progress notif and same app, only update ui
            val isPerPrgNotif =
                (perNotifApp != null && (notification.progressIndeterminate || notification.progressMax > 0) &&
                        (perNotifApp.progressIndeterminate || perNotifApp.progressMax > 0)
                        && perNotifApp.appName == notification.appName
                        )
            if (isPerPrgNotif)
                addedList.remove(perNotifApp)
            ////
            addedList.add(0, notification)
            _notifications.removeFirst()
            _notifications.add(0, addedList.toList())

            if (isPerPrgNotif.not())
                showSystemNotification(notification)
        } else {
            _notifications.add(0, listOf(notification))
            showSystemNotification(notification)
        }
    }

    private fun showSystemNotification(notification: NotificationData) {
        // Only show system notification if not in silent mode
        if (!_isSilentMode) {
            onShowSystemNotification?.invoke(
                "${notification.packageName.packageToEmoji()} ${notification.appName}: ${notification.title}",
                notification.message
            )
        }
    }

    fun markAsRead(notification: NotificationData): Boolean {
        ///تست نشده
        val notifsGroupIndex =
            _notifications.indexOfFirst { it.first().packageName == notification.packageName }
        if (notifsGroupIndex >= 0) {
            val notifsGroup = _notifications[notifsGroupIndex].toMutableList()
            notifsGroup.remove(notification)
            _notifications.removeAt(notifsGroupIndex)
            _notifications.add(notifsGroupIndex, notifsGroup)
            return true
        }
        return false
    }

    fun markAsRead(notification: List<NotificationData>): Boolean {
        return _notifications.remove(notification)
    }
}