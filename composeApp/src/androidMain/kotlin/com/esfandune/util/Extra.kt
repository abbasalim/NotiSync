package com.esfandune.util

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import com.esfandune.service.NotificationListenerService

fun checkNotificationPermission(context: Context): Boolean {
    val enabledListeners = Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners"
    )
    val myListener = ComponentName(context, NotificationListenerService::class.java)
    return enabledListeners?.contains(myListener.flattenToString()) == true
}