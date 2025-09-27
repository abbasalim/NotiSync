package com.esfandune.service

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
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NotificationService(serverIp: String, serverPort: Int) {
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

    suspend fun getClipboard(): ClipboardData {
        try {
            val clipboardData = client.get("$baseUrl/clipboard") {
                timeout {
                    requestTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                }
            }.body<ClipboardData>()

            Log.d("clipboard", "Received clipboard data: $clipboardData")
            return clipboardData

        } catch (e: Exception) {
            val error = "Error: ${e.message ?: "Unknown error"}"
            Log.e("clipboard", error, e)
            return ClipboardData(error = error)
        }
    }

    suspend fun testConnection(): Boolean {
        ///todo
        return try {
            val response = client.get("/").body<Map<String, String>>()
            response["status"] == "success"
        } catch (e: Exception) {
            Log.e("ConnectionTest", "Failed to connect to server", e)
            false
        }
    }
}