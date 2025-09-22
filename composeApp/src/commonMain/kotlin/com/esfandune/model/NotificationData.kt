package com.esfandune.model

import kotlinx.serialization.Serializable

//appIcon >base64
@Serializable
data class NotificationData(
    val title: String,
    val appName: String,
//    val appIcon: String?,
    val message: String,
    val packageName: String,
    val timestamp: Long = System.currentTimeMillis()
)