package com.fsp.plantapp

import com.fsp.plantapp.diagram.Diagram
import com.fsp.plantapp.diagram.DiagramService
import io.github.francois389.javaspringfx.annotations.ViewModel
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList

@ViewModel
class MainViewModel(
    private val diagramService: DiagramService
) {
    val diagrams: ObservableList<Diagram> =
        FXCollections.observableArrayList(diagramService.getAll())

    init {
        diagramService.onNouveauDiagramms { diagram ->
            Platform.runLater {
                if (diagrams.none { it.id == diagram.id }) {
                    diagrams.add(diagram)
                }
            }
        }
    }

    fun removeDiagram(diagram: Diagram) {
        diagrams.remove(diagram)
        // + appeler diagramService pour suppression persistée si besoin
    }
}