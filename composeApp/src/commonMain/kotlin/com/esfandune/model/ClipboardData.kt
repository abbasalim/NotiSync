package com.esfandune.model

import kotlinx.serialization.Serializable

//appIcon >base64
@Serializable
data class ClipboardData(
    val content: String?,
    val error: String?
)