package com.fsp.plantapp.main

import com.fsp.plantapp.Navigator
import com.fsp.plantapp.Screen
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

class MainView: TabPane() {
    init {
        tabs.addAll(
            Tab("Editor", Navigator.getScreen(Screen.EditorScreen)).apply {
                isClosable = false
            },
            Tab("Export", Navigator.getScreen(Screen.ExportScreen)).apply {
                isClosable = false
            }
        )
    }
}