package com.fsp.plantapp.preview

import com.fsp.plantapp.diagram.DiagramService
import io.github.francois389.javaspringfx.annotations.ViewModel
import javafx.beans.property.SimpleStringProperty

@ViewModel
class PreviewViewModel(
    private val diagramService: DiagramService
) {
    val svgDiagram = SimpleStringProperty()

    init {
        svgDiagram.value = diagramService.getSVG()
        diagramService.addObserver {
            svgDiagram.value = diagramService.getSVG()
        }
    }
}
