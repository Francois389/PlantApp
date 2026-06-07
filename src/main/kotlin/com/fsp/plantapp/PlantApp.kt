package com.fsp.plantapp

import com.fsp.plantapp.main.MainView
import org.springframework.boot.autoconfigure.SpringBootApplication
import io.github.francois389.javaspringfx.launchApp

@SpringBootApplication
class PlantApp

fun main() = launchApp<PlantApp>(
    title = "PlantApp",
    icons = listOf("/PlantApp_Logo.png"),
    startingView = MainView::class
)