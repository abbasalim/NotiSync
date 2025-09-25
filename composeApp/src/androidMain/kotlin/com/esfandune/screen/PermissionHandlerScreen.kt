package com.esfandune.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.esfandune.model.PermissionItem
import com.esfandune.util.checkNotificationPermission
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionHandlerScreen(
    modifier: Modifier = Modifier,
    onAllPermissionsGranted: () -> Unit = {}
) {
    val context = LocalContext.current
    val permissionStates = remember { mutableStateMapOf<String, Boolean>() }
    val requiredPermissions = remember {
        mutableListOf<PermissionItem>()
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requiredPermissions.add(
            PermissionItem(
                name = "دسترسی به اعلان‌ها",
                description = "برای نمایش اعلان‌های دریافتی",
                permission = Manifest.permission.POST_NOTIFICATIONS
            )
        )
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        requiredPermissions.add(
            PermissionItem(
                name = "اجازه فعالیت در پس‌زمینه",
                description = "برای دریافت اعلان‌ها در پس‌زمینه",
                permission = Manifest.permission.FOREGROUND_SERVICE
            )
        )
    }


    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { (permission, isGranted) ->
            permissionStates[permission] = isGranted
        }
        // If all permissions are granted, navigate away
        if (checkAllPermissionsGranted(permissionStates, context)) {
            onAllPermissionsGranted()
        }
    }

    // Check current permission status
    LaunchedEffect(Unit) {
        requiredPermissions.forEach { permission ->
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                permission.permission
            ) == PackageManager.PERMISSION_GRANTED
            permissionStates[permission.permission] = isGranted
        }
        if (checkAllPermissionsGranted(permissionStates, context)) {
            onAllPermissionsGranted()
        }
    }



    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "مجوزهای مورد نیاز",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            if (checkAllPermissionsGranted(permissionStates,context).not()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Button(
                            onClick = {
                                if (checkNotificationPermission(context).not()) {
                                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                    context.startActivity(intent)
                                }
                                val permissionsToRequest = requiredPermissions
                                    .filter { !permissionStates.getOrDefault(it.permission, false) }
                                    .map { it.permission }
                                    .toTypedArray()
                                permissionLauncher.launch(permissionsToRequest)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text("درخواست دسترسی‌ها", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "برای استفاده از تمام قابلیت‌های برنامه، لطفاً دسترسی‌های زیر را تأیید کنید:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(requiredPermissions) { permission ->
                val isGranted = permissionStates.getOrDefault(permission.permission, false)
                PermissionItemCard(
                    permission = permission,
                    isGranted = isGranted
                )
            }
            item {
                PermissionItemCard(
                    permission = PermissionItem(
                        name = "دسترسی خواندن اعلان‌ها",
                        description = "برای ارسال اعلان‌های به کامپیوتر",
                        permission = " "
                    ),
                    isGranted = checkNotificationPermission(context)
                )
            }


            item {
                if (checkAllPermissionsGranted(permissionStates,context)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✅ تمام دسترسی‌های مورد نیاز تأیید شدند",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Button(onClick = { onAllPermissionsGranted() }) { Text("بزن بریم") }
                    }
                }
            }
        }
    }


}


private fun checkAllPermissionsGranted(
    permissionStates: SnapshotStateMap<String, Boolean>,
    context: Context
): Boolean = permissionStates.all { it.value } && checkNotificationPermission(context)


@Composable
private fun PermissionItemCard(
    permission: PermissionItem,
    isGranted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isGranted) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isGranted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = permission.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isGranted) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Text(
                    text = permission.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isGranted) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (isGranted) {
                Text(
                    text = "تأیید شد",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}



