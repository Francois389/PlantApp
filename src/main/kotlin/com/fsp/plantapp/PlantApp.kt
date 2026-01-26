package com.fsp.plantapp

import com.fsp.plantapp.Screen.MainScreen
import com.fsp.plantapp.main.MainView
import com.fsp.plantapp.main.MainViewModel
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

class PlantApp : Application() {
    override fun start(stage: Stage) {
        val navigator = Navigator(stage)
        navigator.viewFactory = { screen ->
            when (screen) {
                is MainScreen -> MainView(MainViewModel())
            }
        }

        stage.apply {
            title = "Plant App"
            scene = Scene(Pane(), 10.0, 10.0)
            show()
        }

        navigator.navigateTo(MainScreen)
    }
}
  
fun main() {
    Application.launch(PlantApp::class.java)
}