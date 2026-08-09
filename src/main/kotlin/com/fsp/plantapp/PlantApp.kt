package com.fsp.plantapp

import io.github.francois389.javaspringfx.launchApp
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class PlantApp


object Launcher {
    @JvmStatic
    fun main(args: Array<String>) = launchApp<PlantApp>(
        title = "PlantApp",
        icons = listOf("/PlantApp_Logo.png"),
        startingView = MainView::class
    )
}