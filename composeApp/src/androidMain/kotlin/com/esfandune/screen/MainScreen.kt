package com.esfandune.screen


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.SettingsInputComponent
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.esfandune.component.selector.AppSelectorView
import com.esfandune.screen.component.ClipboardContentDialog
import com.esfandune.screen.component.MainTopBar
import com.esfandune.screen.component.QrScannerDialog
import com.esfandune.ui.ButtonCard
import com.esfandune.ui.HelpDialog
import com.esfandune.ui.StatItem
import com.esfandune.util.rememberWiFiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel: MainScreenViewModel = viewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = .9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )


    var tempSelectedExcludedPackages by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showPermissionHandler by remember { mutableStateOf(true) }
    if (showPermissionHandler) {
        PermissionHandlerScreen {
            viewModel.initializeWithContext(context)
            showPermissionHandler = false
        }
        return
    }

    LaunchedEffect(uiState.serverIp, uiState.serverPort) {
        viewModel.serverIp.value = uiState.serverIp
        viewModel.serverPort.value = uiState.serverPort.toString()
        if (viewModel.serverIp.value.isBlank())
            viewModel.showServerSettings.value = true
    }

    LaunchedEffect(uiState.excludedPackages) {
        tempSelectedExcludedPackages = uiState.excludedPackages
    }

    // Show snackbar when status message changes
    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearStatusMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            MainTopBar {
                viewModel.showServerSettings.value = !viewModel.showServerSettings.value
            }
        },
        floatingActionButton = {
            FAB(viewModel)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //Help Card
                ButtonCard(
                    modifier = Modifier
                        .weight(1f)
                        .scale(if (viewModel.serverIp.value.isBlank()) scale else 1f),
                    title = "راهنمای اتصال",
                    status = "مشاهده",
                    icon = Icons.AutoMirrored.Filled.Help,
                    isActive = true,
                    onClick = {
                        viewModel.showHelDialog.value = true
                    }
                )

                //Notif card
                ButtonCard(
                    modifier = Modifier.weight(1f),
                    title = "عدم ارسال اعلان برای",
                    status = "${uiState.excludedPackages.size} برنامه ",
                    icon = Icons.Outlined.NotificationsOff,
                    isActive = uiState.excludedPackages.isNotEmpty(),
                    onClick = {
                        tempSelectedExcludedPackages = uiState.excludedPackages
                        viewModel.showAppSelectorDialog.value = true
                    }
                )
            }

            // Server Settings Section
            AnimatedVisibility(viewModel.showServerSettings.value) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SettingsInputComponent,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "تنظیمات سرور",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = viewModel.serverIp.value,
                                onValueChange = { viewModel.serverIp.value = it },
                                label = { Text("آدرس IP سرور") },
                                modifier = Modifier.weight(2f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = viewModel.serverPort.value,
                                onValueChange = { viewModel.serverPort.value = it },
                                label = { Text("پورت سرور") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = { viewModel.showQrScanner.value = true },
                                modifier = Modifier.height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.QrCode,
                                    contentDescription = "اسکن QR کد"
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.saveSettings(
                                    serverIp = viewModel.serverIp.value,
                                    serverPort = viewModel.serverPort.value.toIntOrNull() ?: 8080
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("ذخیره تنظیمات سرور")
                        }
                    }
                }
            }

            // Statistics Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Storage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "آمار",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    StatItem(
                        label = "نوتیفیکیشن‌های ارسال شده:",
                        value = uiState.notificationsSent.toString()
                    )

                    StatItem(
                        label = "آخرین ارسال:",
                        value = uiState.lastConnectionTime.ifEmpty { "هرگز" }
                    )

                }
            }

            if (viewModel.showAppSelectorDialog.value) {
                Dialog(
                    onDismissRequest = { viewModel.showAppSelectorDialog.value = false },
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
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "انتخاب برنامه‌های مستثنی",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                AppSelectorView(
                                    preselectedPackages = tempSelectedExcludedPackages.toList(),
                                    onSelectionChanged = { updatedSelection ->
                                        tempSelectedExcludedPackages = updatedSelection.toSet()
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.showAppSelectorDialog.value = false },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text("انصراف")
                                }

                                Button(
                                    onClick = {
                                        viewModel.saveExcludedPackages(tempSelectedExcludedPackages.toList())
                                        viewModel.showAppSelectorDialog.value = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text("ذخیره")
                                }
                            }
                        }
                    }
                }
            }


        }
    }
    if (viewModel.showHelDialog.value) {
        HelpDialog { viewModel.showHelDialog.value = false }
    }
    // QR Scanner Dialog
    if (viewModel.showQrScanner.value) {
        QrScannerDialog(
            onDismiss = { viewModel.showQrScanner.value = false },
            onResult = { ip, port ->
                viewModel.serverIp.value = ip
                viewModel.serverPort.value = port
                viewModel.showQrScanner.value = false
            }
        )
    }

    viewModel.lastClipboardData.value?.let { data ->
        ClipboardContentDialog(
            clipboardData = data,
            onDismiss = { viewModel.lastClipboardData.value = null },
        )
    }
}

@Composable
private fun FAB(
    viewModel: MainScreenViewModel
) {
    val isWifiConnected = rememberWiFiState().value
    val context = LocalContext.current
    FloatingActionButton(
        onClick = {
            if (isWifiConnected)
                viewModel.getClipboard(context)
            else viewModel.showMessage("به شبکه Wi-Fi متصل نیتسید!")
        },
    ) {
        AnimatedContent(viewModel.receivingClipboard.value) {
            if (it) {
                CircularProgressIndicator()
            } else {
                Icon(
                    imageVector = Icons.Outlined.ContentPaste,
                    contentDescription = "get Server Clipboard"
                )
            }
        }
    }
}






