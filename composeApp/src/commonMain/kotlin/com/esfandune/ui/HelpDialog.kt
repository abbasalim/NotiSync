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
                    text = "راهنمای کار با برنامه",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "0. برنامه دسکتاپ را از سایت زیر نصب نمایید:",
                )
                OutlinedButton(onClick = { uriHandler.openUri("http://tools.esfandune.ir") }) {
                    Text(
                        text = "http://tools.esfandune.ir",
                    )
                }

                Text(
                    text = "1. راهنمای نصب و راه‌اندازی دسکتاپ:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = " برنامه دسکتاپ را اجرا کرده، برروی تنظیمات بزنید و برروی علامت بارکد کلیک کنید.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Desktop Help Image
                Image(
                    painter = painterResource(Res.drawable.hlp_desktop),
                    contentDescription = "راهنمای دسکتاپ",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Text 2
                Text(
                    text = "2. اتصال دستگاه موبایل:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = " در برنامه موبایل، برروی تنظیمات زده و علامت بارکد را لمس کنید تا دوربین باز شده و آن را جلو بارکد قرار دهید. بعد از تکمیل آی پی و پورت برروی علامت (+) جهت ذخیره کلیک نمایید.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Mobile Help Image
                Image(
                    painter = painterResource(Res.drawable.hlp_mobile),
                    contentDescription = "راهنمای موبایل",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Text 3
                Text(
                    text = "3. شروع استفاده:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "در صورت اتصال موفق، می‌توانید از قابلیت‌های برنامه استفاده کنید. اعلان‌های شما به صورت خودکار همگام‌سازی می‌شوند.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("متوجه شدم")
                }
            }
        }
    }
}