package com.fsp.plantapp

import com.fsp.plantapp.Screen.*
import com.fsp.plantapp.diagram.DiagramService
import com.fsp.plantapp.diagram.InMemoryDiagramRepository
import com.fsp.plantapp.editor.EditorView
import com.fsp.plantapp.editor.EditorViewModel
import com.fsp.plantapp.export.ExportView
import com.fsp.plantapp.export.ExportViewModel
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

class PlantApp : Application() {
    override fun start(stage: Stage) {
        val diagramRepository = InMemoryDiagramRepository()
        val diagramService = DiagramService(diagramRepository)
        val navigator = Navigator
        navigator.setup(stage)
        navigator.viewFactory = { screen ->
            when (screen) {
                is EditorScreen -> EditorView(EditorViewModel(diagramService))
                is ExportScreen -> ExportView(ExportViewModel(diagramService))
            }
        }

        stage.apply {
            title = "Plant App"
            scene = Scene(Pane(), 10.0, 10.0)
            show()
        }

        navigator.navigateTo(EditorScreen)
    }
}

fun main() {
    Application.launch(PlantApp::class.java)
}