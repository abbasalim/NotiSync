package com.esfandune


import androidx.compose.ui.text.toLowerCase
import com.esfandune.model.ClipboardData
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
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO
import java.io.File
import java.nio.file.Files
import java.awt.Image
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage

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
                get("/clipboard") {
                    sendClipboard()
                }
            }
        }
        server?.start(wait = false)
        println("Server started on port $port")
    }

    private suspend fun RoutingContext.sendClipboard() {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val transferable = clipboard.getContents(null)

            when {
                // First check for file list
                transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor) -> {
                    val fileList =
                        transferable.getTransferData(DataFlavor.javaFileListFlavor) as? List<*>
                    if (!fileList.isNullOrEmpty()) {
                        val file = fileList.first() as? File
                        if (file != null) {
                            // Check if the file is an image
                            val mimeType = Files.probeContentType(file.toPath())?.lowercase()
                            val isImageFile = mimeType?.startsWith("image/") == true

                            if (isImageFile) {
                                try {
                                    // Try to read the image file
                                    val image = ImageIO.read(file)
                                    if (image != null) {
                                        val outputStream = ByteArrayOutputStream()
                                        val formatName = when (mimeType) {
                                            "image/jpeg" -> "jpg"
                                            "image/png" -> "png"
                                            "image/gif" -> "gif"
                                            "image/bmp" -> "bmp"
                                            else -> "png" // default to png if unknown
                                        }
                                        ImageIO.write(image, formatName, outputStream)
                                        val imageBytes = outputStream.toByteArray()
                                        val base64Image =
                                            Base64.getEncoder().encodeToString(imageBytes)

                                        call.respond(
                                            ClipboardData.createImage(
                                                base64Image,
                                                mimeType ?: "image/png"
                                            )
                                        )
                                        return
                                    }
                                } catch (e: Exception) {
                                    // If we can't process as image, fall back to regular file handling
                                    println("Failed to process as image, falling back to file: ${e.message}")
                                }
                            }

                            // If not an image or image processing failed, handle as regular file
                            val fileBytes = Files.readAllBytes(file.toPath())
                            val base64File = Base64.getEncoder().encodeToString(fileBytes)
                            val resolvedMimeType = mimeType ?: "application/octet-stream"

                            call.respond(
                                ClipboardData.createFile(
                                    fileData = base64File,
                                    fileName = file.name,
                                    mimeType = resolvedMimeType
                                )
                            )
                        } else {
                            call.respond(ClipboardData.createError("Failed to process file from clipboard"))
                        }
                    } else {
                        call.respond(ClipboardData.createError("No files found in clipboard"))
                    }
                }
                // Then check for image data
                transferable.isDataFlavorSupported(DataFlavor.imageFlavor) -> {
                    val image = transferable.getTransferData(DataFlavor.imageFlavor) as? Image
                    if (image != null) {
                        val bufferedImage = if (image is BufferedImage) {
                            image
                        } else {
                            val bufferedImage = BufferedImage(
                                image.getWidth(null),
                                image.getHeight(null),
                                BufferedImage.TYPE_INT_ARGB
                            )
                            val g = bufferedImage.createGraphics()
                            g.drawImage(image, 0, 0, null)
                            g.dispose()
                            bufferedImage
                        }

                        val outputStream = ByteArrayOutputStream()
                        ImageIO.write(bufferedImage, "png", outputStream)
                        val imageBytes = outputStream.toByteArray()
                        val base64Image = Base64.getEncoder().encodeToString(imageBytes)

                        call.respond(ClipboardData.createImage(base64Image, "image/png"))
                    } else {
                        call.respond(ClipboardData.createError("Failed to get image from clipboard"))
                    }
                }
                // Finally check for text
                transferable.isDataFlavorSupported(DataFlavor.stringFlavor) -> {
                    val text = transferable.getTransferData(DataFlavor.stringFlavor) as? String
                    if (!text.isNullOrEmpty()) {
                        call.respond(ClipboardData.createText(text))
                    } else {
                        call.respond(ClipboardData.createError("Clipboard is empty or doesn't contain text"))
                    }
                }

                else -> {
                    call.respond(ClipboardData.createError("Unsupported clipboard content type"))
                }
            }
        } catch (e: Exception) {
            call.respond(ClipboardData.createError("Failed to access clipboard: ${e.message}"))
        }
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