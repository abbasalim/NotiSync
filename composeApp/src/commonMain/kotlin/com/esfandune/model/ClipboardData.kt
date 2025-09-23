package com.esfandune.model

import kotlinx.serialization.Serializable

@Serializable
data class ClipboardData(
    val text: String? = null,
    val imageData: String? = null,  // Base64 encoded image data
    val fileData: String? = null,   // Base64 encoded file data
    val fileName: String? = null,   // Original file name if available
    val mimeType: String? = null,   // MIME type of the content
    val error: String? = null
) {
    companion object {
        fun createText(text: String) = ClipboardData(text = text, mimeType = "text/plain")
        fun createImage(imageData: String, mimeType: String) = 
            ClipboardData(imageData = imageData, mimeType = mimeType)
        fun createFile(fileData: String, fileName: String, mimeType: String) = 
            ClipboardData(fileData = fileData, fileName = fileName, mimeType = mimeType)
        fun createError(error: String) = ClipboardData(error = error)
    }
}