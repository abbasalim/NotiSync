package com.esfandune.service

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import com.esfandune.model.ClipboardData
import com.esfandune.model.NotificationData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class NotificationService( serverIp: String,  serverPort: Int) {
    val baseUrl = "http://$serverIp:$serverPort"
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun sendNotification(
        data: NotificationData
    ): Boolean {
        return try {
            val response: HttpResponse = client.post("$baseUrl/notification") {
                contentType(ContentType.Application.Json)
                setBody(data)
                timeout {
                    requestTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                }
            }

            response.status.isSuccess()

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getClipboard(context: Context): ClipboardData {
        try {
            val clipboardData = client.get("$baseUrl/clipboard") {
                timeout {
                    requestTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                }
            }.body<ClipboardData>()
            
            Log.d("clipboard", "Response: $clipboardData")

            clipboardData.content?.let { content ->
                // Copy to clipboard
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("clipboard_content", content)
                clipboard.setPrimaryClip(clip)
                Log.d("clipboard", "Copied to clipboard: $content")
            } ?: {
                val errorMessage = clipboardData.error ?: "No content in clipboard"
                Log.d("clipboard", errorMessage)
            }
            return clipboardData

        } catch (e: Exception) {
            val error = "Error: ${e.message ?: "Unknown error"}"
            Log.e("clipboard", error, e)
            return ClipboardData(null, error)
        }
    }
}