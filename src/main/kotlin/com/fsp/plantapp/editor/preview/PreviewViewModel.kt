package com.fsp.plantapp.editor.preview

import com.fsp.plantapp.diagram.DiagramService
import io.github.francois389.javaspringfx.annotations.ViewModel
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty

@ViewModel
class PreviewViewModel(
    private val diagramService: DiagramService
) {
    val svgDiagram = SimpleStringProperty()

    val zoomLevel = SimpleDoubleProperty(1.0)

    init {
        svgDiagram.value = diagramService.getSVG()
        diagramService.addObserver {
            svgDiagram.value = diagramService.getSVG()
        }
    }
}
