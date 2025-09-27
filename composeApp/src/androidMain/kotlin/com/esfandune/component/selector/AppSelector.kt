package com.esfandune.component.selector

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.launch

@Composable
fun AppSelectorView(
    preselectedPackages: List<String> = emptyList(),
    onSelectionChanged: (List<String>) -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var showSystemApps by remember { mutableStateOf(true) }
    var appsList by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var selectedPackages by remember { mutableStateOf(preselectedPackages.toSet()) }
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(showSystemApps) {
        scope.launch {
            isLoading = true
            appsList = getInstalledApps(context, showSystemApps)
                .sortedByDescending { it.packageName in selectedPackages }
            isLoading = false
        }
    }

    val filteredApps = remember(appsList, searchQuery.text) {
        if (searchQuery.text.isEmpty()) {
            appsList
        } else {
            val query = searchQuery.text.lowercase()
            appsList.filter {
                it.name.lowercase().contains(query) ||
                        it.packageName.lowercase().contains(query)
            }
        }
    }

    if (isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("انتخاب کنید، اعلان مربوط به کدام برنامه ها برای دسکتاپ ارسال نشود:")
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search apps") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true
            )
            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .clickable { showSystemApps = !showSystemApps },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(showSystemApps, onCheckedChange = { showSystemApps = it })
                Text("نمایش برنامه‌‌های سیستمی")
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredApps, key = { it.packageName }) { appInfo ->
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
            .padding(vertical = 8.dp, horizontal = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = appInfo.icon.toBitmap().asImageBitmap(),
            contentDescription = appInfo.name,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier .weight(1f)) {
        Text(
            text = appInfo.name,
            modifier = Modifier.fillMaxWidth().basicMarquee(),
            maxLines = 1
        )
        Text(
            text = appInfo.packageName,
            modifier = Modifier.fillMaxWidth().basicMarquee().alpha(0.5f),
            maxLines = 1
        )
        }
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onAppSelected(appInfo.packageName, it) }
        )
    }
}

private fun getInstalledApps(context: Context, showSystemApps: Boolean): List<AppInfo> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val resolveInfoList = pm.queryIntentActivities(intent, 0)
    val apps = mutableListOf<AppInfo>()

    for (resolveInfo in resolveInfoList) {
        val appInfo = resolveInfo.activityInfo.applicationInfo
        // Non-system apps or updated system apps
        if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 || showSystemApps) {
            val appName = appInfo.loadLabel(pm).toString()
            val packageName = appInfo.packageName
            val icon = appInfo.loadIcon(pm)
            apps.add(AppInfo(appName, packageName, icon))
        }
    }
    return apps.sortedBy { it.name.lowercase() }
}
