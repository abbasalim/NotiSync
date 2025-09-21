package com.esfandune


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.esfandune.model.NotificationData
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class NotificationServer(private val notificationManager: NotificationManager) {
    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? =
        null

    fun start(port: Int = 8080) {
        server = embeddedServer(Netty, port = port) {
            install(ContentNegotiation) {
                json()
            }

            routing {
                post("/notification") {
                    try {
                        val notification = call.receive<NotificationData>()
                        notificationManager.addNotification(notification)
                        call.respond(mapOf("status" to "success"))
                    } catch (e: Exception) {
                        call.respond(mapOf("status" to "error", "message" to e.message))
                    }
                }

                post("/mark-read") {
                    try {
                        val notification = call.receive<NotificationData>()
                        val removed = notificationManager.markAsRead(notification)
                        call.respond(mapOf("status" to if (removed) "success" else "not_found"))
                    } catch (e: Exception) {
                        call.respond(mapOf("status" to "error", "message" to e.message))
                    }
                }

                get("/") {
                    call.respondText("Notification Server is running!")
                }
            }
        }
        server?.start(wait = false)
        println("Server started on port $port")
    }

    fun stop() {
        server?.stop(1000, 2000)
    }
}