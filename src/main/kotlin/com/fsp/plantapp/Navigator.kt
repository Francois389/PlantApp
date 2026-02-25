package com.fsp.plantapp

import javafx.scene.Parent
import javafx.stage.Stage

object Navigator {
    private lateinit var primaryStage: Stage
    private var currentScreen: Screen? = null
    private val viewCache = mutableMapOf<Screen, Parent>()

    fun setup(stage: Stage) {
        primaryStage = stage
    }

    lateinit var viewFactory: (Screen) -> Parent

    fun navigateTo(screen: Screen, reloadView: Boolean = false) {
        if (currentScreen == screen) return

        currentScreen = screen

        if (reloadView) viewCache[screen] = viewFactory(screen)
        val view = viewCache.getOrPut(screen) { viewFactory(screen) }

        primaryStage.apply {
            scene.root = view
            width = screen.width
            height = screen.heigth
        }
    }

    fun getScreen(screen: Screen): Parent {
        return viewCache.getOrPut(screen) { viewFactory(screen) }
    }
}