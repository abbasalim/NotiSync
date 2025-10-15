package com.esfandune.model

data class UiState(
    val notificationsSent: Int = 0,
    val lastConnectionTime: String = "",
    val statusMessage: String? = null,
    val excludedPackages: Set<String> = emptySet(),
    val serverAddress: List<String> = emptyList()
)
