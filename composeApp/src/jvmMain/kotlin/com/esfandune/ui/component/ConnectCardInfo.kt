package com.esfandune.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.esfandune.ui.QRCodeDialog
import com.esfandune.util.getDeviceIp

@Composable
fun ConnectCardInfo(port:Int) {
    var showQRDialog by remember { mutableStateOf(false) }
    if (showQRDialog) {
        QRCodeDialog(
            url = "http://${getDeviceIp()}:$port",
            onDismiss = { showQRDialog = false }
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "http://${getDeviceIp()}:$port",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { showQRDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "نمایش QR Code",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

