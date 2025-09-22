package com.esfandune


import androidx.compose.ui.text.toLowerCase
import com.esfandune.model.NotificationData
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

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
                        if (isNotExclude(notification))
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

    private fun isNotExclude(notification: NotificationData): Boolean {
        if (notification.appName.lowercase() in listOf<String>("system ui","واسط کاربری سیستم")) {
            println("${notification.packageName} ${notification.appName} is exclude")
            return false
        } else {
            println("${notification.packageName} ${notification.appName} is not exclude")
            return true
        }
    }

    fun stop() {
        server?.stop(1000, 2000)
    }
}