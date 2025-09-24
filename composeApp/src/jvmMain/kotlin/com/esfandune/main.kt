package com.esfandune

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.esfandune.ui.MainApp
import notisync.composeapp.generated.resources.Res
import notisync.composeapp.generated.resources.icon_dark
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    val icon = painterResource(Res.drawable.icon_dark)
    Window(
        icon = icon,
        onCloseRequest = ::exitApplication,
        title = "NotiSync",
    ) {
        MainApp()
    }
}