package com.esfandune.service

import com.esfandune.model.NotificationData
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.timeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NotificationService {
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
        data: NotificationData,
        serverIp: String,
        serverPort: Int
    ): Boolean {
        return try {
            val response: HttpResponse = client.post("http://$serverIp:$serverPort/notification") {
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
}