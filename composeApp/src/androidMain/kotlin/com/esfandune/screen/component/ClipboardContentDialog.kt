package com.esfandune.screen.component

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.esfandune.R
import com.esfandune.model.ClipboardData
import java.io.File
import java.io.FileOutputStream


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipboardContentDialog(
    clipboardData: ClipboardData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val hasImage = !clipboardData.imageData.isNullOrEmpty()
    val hasFile = !clipboardData.fileData.isNullOrEmpty()
    val hasText = !clipboardData.text.isNullOrEmpty()
    val shareWithLabel = stringResource(R.string.share_with)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (hasImage) {
                    stringResource(R.string.clipboard_title_image)
                } else if (hasFile) {
                    stringResource(R.string.clipboard_title_file)
                } else {
                    stringResource(R.string.clipboard_title_text)
                },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (hasImage) {
                    // Show image preview
                    val imageBytes = Base64.decode(clipboardData.imageData, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.clipboard_image_desc),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                } else if (hasFile) {
                    // Show file icon
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.FileCopy,
                            contentDescription = stringResource(R.string.clipboard_file_desc),
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (hasText) {
                    Text(
                        text = clipboardData.text.take(200) + if (clipboardData.text.length > 200) "..." else "",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
                // Copy Button
            FilledTonalButton(
                onClick = {
                    (context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
                        when {
                            hasImage -> {
                                val imageBytes = Base64.decode(clipboardData.imageData, Base64.DEFAULT)
                                val tempFile = File(context.cacheDir, "clipped_image.png")
                                FileOutputStream(tempFile).use { it.write(imageBytes) }
                                val contentUri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    tempFile
                                )
                                val clip = ClipData.newUri(
                                    context.contentResolver,
                                    "Image",
                                    contentUri
                                )
                                clipboard.setPrimaryClip(clip)
                            }
                            hasFile -> {
                                val fileBytes = Base64.decode(clipboardData.fileData, Base64.DEFAULT)
                                val extension = clipboardData.fileName?.substringAfterLast('.', "bin") ?: "bin"
                                val tempFile = File(context.cacheDir, "clipped_file.$extension")
                                FileOutputStream(tempFile).use { it.write(fileBytes) }
                                val contentUri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    tempFile
                                )
                                val clip = ClipData.newUri(
                                    context.contentResolver,
                                    "File",
                                    contentUri
                                )
                                clipboard.setPrimaryClip(clip)
                            }
                            else -> {
                                val clip = ClipData.newPlainText("Text", clipboardData.text ?: "")
                                clipboard.setPrimaryClip(clip)
                            }
                        }
                    }
                },
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = stringResource(R.string.copy),
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.copy))
            }

        },
        dismissButton = {
            Button(
                onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        if (hasImage) {
                            // For image sharing
                            val imageBytes =
                                Base64.decode(clipboardData.imageData, Base64.DEFAULT)
                            val tempFile = File(context.cacheDir, "shared_image.png")
                            FileOutputStream(tempFile).use { it.write(imageBytes) }
                            val contentUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                tempFile
                            )
                            putExtra(Intent.EXTRA_STREAM, contentUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            type = "image/*"
                        } else if (hasFile) {
                            ///todo
                        } else {
                            // For text sharing
                            putExtra(Intent.EXTRA_TEXT, clipboardData.text)
                            type = "text/plain"
                        }
                    }
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            shareWithLabel
                        )
                    )
                },
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = stringResource(R.string.share),
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.share))
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}
