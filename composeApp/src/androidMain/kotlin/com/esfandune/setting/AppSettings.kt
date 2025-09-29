package com.esfandune.setting

data class AppSettings(
    val serverIp: String = "",
    val serverPort: Int = 8080,
    val notificationsSent: Int = 0,
    val lastConnectionTime: String = "",
    val excludedPackages: Set<String> = emptySet()
)
