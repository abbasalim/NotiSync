package com.esfandune.setting

data class AppSettings(
    val serverIp: String = "192.168.1.22",
    val serverPort: Int = 8080,
    val notificationsSent: Int = 0,
    val lastConnectionTime: String = ""
)