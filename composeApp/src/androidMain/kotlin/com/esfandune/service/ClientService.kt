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
import io.ktor.client.request.head
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * This class is responsible for communicating with the desktop application
 * and sending notifications to it.
 *
 * @param serverAddress Set of IP addresses of the desktop applications
 */
class ClientService(private val serverAddress: Set<String>) {
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
    ): Boolean = withContext(Dispatchers.IO) {
        if (serverAddress.isEmpty()) return@withContext false

        serverAddress.map { address ->
            async {
                try {
                    val response: HttpResponse = client.post("http://$address/notification") {
                        contentType(ContentType.Application.Json)
                        setBody(data)
                        timeout {
                            requestTimeoutMillis = 2000
                        }
                    }
                    response.status.isSuccess()
                } catch (e: Exception) {
                    Log.e("sendNotification", "Failed to send notification to $address", e)
                    false
                }
            }
        }.awaitAll().any { it }
    }

    suspend fun getClipboard(limitSize: Long? = null): ClipboardData {
        if (serverAddress.isEmpty()) return ClipboardData(error = "No server address configured")

        for (address in serverAddress) {
            try {
                val baseUrl = "http://$address"
                if (limitSize != null) {
                    val contentLength = client.head("$baseUrl/clipboard") {
                        timeout {
                            requestTimeoutMillis = 2000
                        }
                    }.headers["Content-Length"]?.toLongOrNull()
                    Log.d("clipboard", "Content-Length from $address: ${contentLength ?: "unknown"}")
                    if (limitSize < (contentLength ?: 0)) {
                        Log.w("clipboard", "Content from $address is too large, trying next.")
                        continue
                    }
                }

                val clipboardData = client.get("$baseUrl/clipboard") {
                    timeout {
                        requestTimeoutMillis = 5000
                    }
                }.body<ClipboardData>()

                if (clipboardData.text?.isNotEmpty() == true) {
                    Log.d("clipboard", "Received clipboard data from $address")
                    return clipboardData
                }
            } catch (e: Exception) {
                Log.e("clipboard", "Failed to get clipboard from $address", e)
            }
        }
        return ClipboardData(error = "Failed to get clipboard from any server")
    }

    suspend fun testConnection(): List<Pair<String, Boolean>> = withContext(Dispatchers.IO) {
        if (serverAddress.isEmpty()) return@withContext emptyList()

        serverAddress.map { address ->
            async {
                val isConnected = try {
                    val response = client.get("http://$address").body<Map<String, String>>()
                    response["status"] == "success"
                } catch (e: Exception) {
                    Log.e("ConnectionTest", "Failed to connect to server http://$address", e)
                    false
                }
                address to isConnected
            }
        }.awaitAll()
    }
}

