package com.esfandune.screen.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esfandune.R
import com.esfandune.util.rememberWiFiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(onSettingsClick: () -> Unit) {
    val isWifiConnected = rememberWiFiState().value

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        actions = {
            // WiFi status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                if (isWifiConnected) {
                    Icon(
                        imageVector = Icons.Outlined.Wifi,
                        contentDescription = stringResource(R.string.wifi_connected_desc),
                        tint = MaterialTheme.colorScheme.primary
                    )

                } else {
                    Icon(
                        imageVector = Icons.Outlined.WifiOff,
                        contentDescription = stringResource(R.string.wifi_disconnected_desc),
                        tint = MaterialTheme.colorScheme.tertiary
                    )

                }
            }
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.server_settings_desc)
                )
            }
        }
    )
}
