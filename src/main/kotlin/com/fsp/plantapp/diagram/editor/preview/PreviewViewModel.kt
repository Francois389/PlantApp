package com.fsp.plantapp.editor.preview

import com.fsp.plantapp.diagram.Diagram
import io.github.francois389.javaspringfx.annotations.ViewModel
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty

class PreviewViewModel(
    private val diagram: Diagram
) {
    val svgDiagram = SimpleStringProperty()

    val zoomLevel = SimpleDoubleProperty(1.0)

    init {
        svgDiagram.value = diagram.getSVG()
        diagram.addObserver {
            svgDiagram.value = diagram.getSVG()
        }
    }
}
