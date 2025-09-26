package com.esfandune.screen.component

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern

@Composable
fun QrScannerDialog(
    onDismiss: () -> Unit,
    onResult: (ip: String, port: String) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 24.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "اسکن QR کد",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (!hasCameraPermission) {
                    Text(
                        text = "برای اسکن QR کد، مجوز دسترسی به دوربین مورد نیاز است.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    Button(
                        onClick = { launcher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("درخواست مجوز دوربین")
                    }
                } else {
                    CameraPreview(
                        onBarcodeDetected = { qrText ->
                            val config = parseServerConfig(qrText)
                            if (config != null) {
                                onResult(config.first, config.second)
                                onDismiss()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("انصراف")
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPreview(
    onBarcodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build()
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                
                val barcodeScanner = BarcodeScanning.getClient()
                
                imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageProxy(barcodeScanner, imageProxy, onBarcodeDetected)
                }
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
    )
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onBarcodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { qrText ->
                        onBarcodeDetected(qrText)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("BarcodeScanner", "Barcode scanning failed", exception)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

// تابع کمکی برای پارس کردن QR کد
fun parseServerConfig(qrText: String): Pair<String, String>? {
    // الگوهای مختلف برای QR کد
    val patterns = listOf(
        // IP:Port
        Regex("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d+)"),
        // JSON format
        Regex("""\{.*?"ip":\s*"([^"]+)".*?"port":\s*(\d+).*?\}""", RegexOption.DOT_MATCHES_ALL),
        // Simple format
        Regex("""ip[=:]\s*([^\s,]+).*?port[=:]\s*(\d+)""", RegexOption.IGNORE_CASE)
    )
    
    for (pattern in patterns) {
        val match = pattern.find(qrText)
        if (match != null) {
            val ip = match.groupValues[1]
            val port = match.groupValues[2]
            return Pair(ip, port)
        }
    }
    
    return null
}