package com.esfandune.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationData(
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)