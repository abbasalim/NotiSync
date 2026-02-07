package com.esfandune.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.esfandune.R
import com.esfandune.model.PermissionItem
import com.esfandune.util.checkNotificationPermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionHandlerScreen(
    modifier: Modifier = Modifier,
    onAllPermissionsGranted: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionStates = remember { mutableStateMapOf<String, Boolean>() }
    val requiredPermissions = remember {
        mutableListOf<PermissionItem>()
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requiredPermissions.add(
            PermissionItem(
                name = stringResource(R.string.notif_permission_name),
                description = stringResource(R.string.notif_permission_desc),
                permission = Manifest.permission.POST_NOTIFICATIONS
            )
        )
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        requiredPermissions.add(
            PermissionItem(
                name = stringResource(R.string.background_permission_name),
                description = stringResource(R.string.background_permission_desc),
                permission = Manifest.permission.FOREGROUND_SERVICE
            )
        )
    }


    fun checkAllPermissionsGranted(): Boolean {
        requiredPermissions.forEach { permission ->
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                permission.permission
            ) == PackageManager.PERMISSION_GRANTED
            permissionStates[permission.permission] = isGranted
        }
        return permissionStates.all { it.value } && checkNotificationPermission(context)
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { (permission, isGranted) ->
            permissionStates[permission] = isGranted
        }
        // If all permissions are granted, navigate away
        if (checkAllPermissionsGranted()) {
            onAllPermissionsGranted()
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                if (checkAllPermissionsGranted())
                    onAllPermissionsGranted()
                Log.d("Lifecycle", "Screen Resumed")
            }

            override fun onPause(owner: LifecycleOwner) {
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.permissions_required_title),
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
            if (checkAllPermissionsGranted().not()) {
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
                                    val intent =
                                        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
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
                            Text(stringResource(R.string.request_permissions), fontSize = 16.sp)
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
                    text = stringResource(R.string.permissions_intro),
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
                        name = stringResource(R.string.notif_reader_name),
                        description = stringResource(R.string.notif_reader_desc),
                        permission = " "
                    ),
                    isGranted = checkNotificationPermission(context)
                )
            }


            item {
                if (checkAllPermissionsGranted()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.all_permissions_granted),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Button(onClick = { onAllPermissionsGranted() }) { Text(stringResource(R.string.lets_go)) }
                    }
                }
            }
        }
    }


}


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
                    text = stringResource(R.string.permission_approved),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

