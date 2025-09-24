package com.esfandune

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.WindowState
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
        state = WindowState(
//            position = WindowPosition(100.dp, 100.dp),
            size = DpSize(500.dp, 800.dp)
        )
    ) {
        MainApp()
    }
}