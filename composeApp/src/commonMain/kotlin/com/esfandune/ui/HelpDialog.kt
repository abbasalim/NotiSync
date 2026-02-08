package com.esfandune.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import notisync.composeapp.generated.resources.Res
import notisync.composeapp.generated.resources.hlp_desktop
import notisync.composeapp.generated.resources.hlp_mobile
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpDialog(
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val strings = LocalAppStrings.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                Text(
                    text = strings.helpTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = strings.helpStep0,
                )
                OutlinedButton(onClick = { uriHandler.openUri("http://tools.esfandune.ir") }) {
                    Text(
                        text = "http://tools.esfandune.ir",
                    )
                }

                Text(
                    text = strings.helpStep1Title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = strings.helpStep1Body,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Desktop Help Image
                Image(
                    painter = painterResource(Res.drawable.hlp_desktop),
                    contentDescription = strings.helpDesktopImageDesc,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Text 2
                Text(
                    text = strings.helpStep2Title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = strings.helpStep2Body,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Mobile Help Image
                Image(
                    painter = painterResource(Res.drawable.hlp_mobile),
                    contentDescription = strings.helpMobileImageDesc,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Text 3
                Text(
                    text = strings.helpStep3Title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = strings.helpStep3Body,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(strings.helpUnderstood)
                }
            }
        }
    }
}
