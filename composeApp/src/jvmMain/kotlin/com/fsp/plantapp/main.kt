package com.fsp.plantapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import plantapp_kmp.composeapp.generated.resources.PlantAppLogo
import plantapp_kmp.composeapp.generated.resources.Res

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PlantApp",
        icon = painterResource(Res.drawable.PlantAppLogo)
    ) {
        PlantApp()
    }
}

