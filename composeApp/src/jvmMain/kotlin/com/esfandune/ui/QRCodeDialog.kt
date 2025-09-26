package com.esfandune.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import java.awt.image.BufferedImage
import org.jetbrains.skia.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeDialog(
    url: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QR Code",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // QR Code Image
                val qrCodeBitmap by remember(url) {
                    mutableStateOf(createQRCodeBitmap(url))
                }
                
                qrCodeBitmap?.let { bitmap ->
                    val skiaImage = Image.makeFromEncoded(bitmapToByteArray(bitmap))
                    Image(
                        bitmap = skiaImage.toComposeImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // URL Text
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("بستن")
                }
            }
        }
    }
}

private fun createQRCodeBitmap(text: String): BufferedImage? {
    return try {
        val qrCodeWriter = QRCodeWriter()
        val hints = mapOf<EncodeHintType, Any>(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1
        )
        val bitMatrix: BitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200, hints)
        MatrixToImageWriter.toBufferedImage(bitMatrix)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun bitmapToByteArray(bitmap: BufferedImage): ByteArray {
    val baos = java.io.ByteArrayOutputStream()
    javax.imageio.ImageIO.write(bitmap, "PNG", baos)
    return baos.toByteArray()
}
