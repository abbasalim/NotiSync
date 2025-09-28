package com.esfandune.model

import kotlinx.serialization.Serializable

//appIcon >base64
@Serializable
data class NotificationData(
    val title: String,
    val appName: String,
//    val appIcon: String?,
    val category: NotificationCategory,
    val flags: Int,
    val message: String,
    val progress: Int,
    val progressMax: Int,
    val progressIndeterminate: Boolean,
    val packageName: String,
    val timestamp: Long = System.currentTimeMillis()
)

// برگرفته از package android.app.Notification;
enum class NotificationCategory(val value: String) {
    ALARM("alarm"),
    CALL("call"),
    EMAIL("email"),
    ERROR("err"),
    EVENT("event"),
    LOCATION_SHARING("location_sharing"),
    MESSAGE("msg"),
    MISSED_CALL("missed_call"),
    NAVIGATION("navigation"),
    PROGRESS("progress"),
    PROMO("promo"),
    RECOMMENDATION("recommendation"),
    REMINDER("reminder"),
    SERVICE("service"),
    SOCIAL("social"),
    STATUS("status"),
    STOPWATCH("stopwatch"),
    SYSTEM("system"),
    TRANSPORT("transport"),
    VOICEMAIL("voicemail"),
    WORKOUT("workout")
}

// برگرفته از package android.app.Notification;
enum class NotificationFlags(val value: Int) {
    FLAG_AUTO_CANCEL(16),
    FLAG_BUBBLE(4096),
    FLAG_FOREGROUND_SERVICE(64),
    FLAG_GROUP_SUMMARY(512),
    FLAG_INSISTENT(4),
    FLAG_LOCAL_ONLY(256),
    FLAG_NO_CLEAR(32),
    FLAG_ONGOING_EVENT(2),
    FLAG_ONLY_ALERT_ONCE(8),
    FLAG_PROMOTED_ONGOING(262144),
    @Deprecated("This flag is deprecated.")
    FLAG_HIGH_PRIORITY(128),
    @Deprecated("This flag is deprecated.")
    FLAG_SHOW_LIGHTS(1),
}


