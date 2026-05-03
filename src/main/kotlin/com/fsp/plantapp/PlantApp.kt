package com.fsp.plantapp

import com.fsp.plantapp.main.MainView
import io.github.francois389.javaspringfx.launchApp
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class PlantApp

fun main() = launchApp<PlantApp>(title = "PlantApp") { navigator ->
    navigator.navigateTo(MainView::class)
}