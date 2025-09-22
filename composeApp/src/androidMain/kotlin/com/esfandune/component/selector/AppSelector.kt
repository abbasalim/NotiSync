package com.esfandune.component.selector

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

@Composable
fun AppSelectorView(
    preselectedPackages: List<String> = emptyList(),
    onSelectionChanged: (List<String>) -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var appsList by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var selectedPackages by remember { mutableStateOf(preselectedPackages.toSet()) }

    LaunchedEffect(Unit) {
        isLoading = true
        appsList = getInstalledApps(context)
        isLoading = false
    }

    if (isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(appsList, key = { it.packageName }) { appInfo ->
                AppListItem(
                    appInfo = appInfo,
                    isSelected = appInfo.packageName in selectedPackages,
                    onAppSelected = { packageName, isSelected ->
                        selectedPackages = if (isSelected) {
                            selectedPackages + packageName
                        } else {
                            selectedPackages - packageName
                        }
                        onSelectionChanged(selectedPackages.toList())
                    }
                )
            }
        }
    }
}

@Composable
fun AppListItem(
    appInfo: AppInfo,
    isSelected: Boolean,
    onAppSelected: (String, Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAppSelected(appInfo.packageName, !isSelected) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = appInfo.icon.toBitmap().asImageBitmap(),
            contentDescription = appInfo.name,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = appInfo.name, modifier = Modifier.weight(1f))
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onAppSelected(appInfo.packageName, it) }
        )
    }
}

private fun getInstalledApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val resolveInfoList = pm.queryIntentActivities(intent, 0)
    val apps = mutableListOf<AppInfo>()

    for (resolveInfo in resolveInfoList) {
        val appInfo = resolveInfo.activityInfo.applicationInfo
        // Non-system apps or updated system apps
        if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) { 
            val appName = appInfo.loadLabel(pm).toString()
            val packageName = appInfo.packageName
            val icon = appInfo.loadIcon(pm)
            apps.add(AppInfo(appName, packageName, icon))
        }
    }
    return apps.sortedBy { it.name.lowercase() }
}
