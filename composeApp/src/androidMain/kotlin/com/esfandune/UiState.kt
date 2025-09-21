package com.esfandune


data class UiState(
    val hasNotificationPermission: Boolean = false,
    val isServiceRunning: Boolean = false,
    val serverIp: String = "192.168.1.22",
    val serverPort: Int = 8080,
    val notificationsSent: Int = 0,
    val lastConnectionTime: String = "",
    val statusMessage: String? = null
)