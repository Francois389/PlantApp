package com.fsp.plantapp.editor

import com.fsp.plantapp.diagram.DiagramService
import com.fsp.plantapp.diagram.PlantUMLDiagram
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class EditorViewModel(
    private val diagramService: DiagramService
) {
    val source = diagramService.getDiagram().source
    val image: Property<ByteArray> = SimpleObjectProperty()
    val title = SimpleStringProperty()

    val svgContent = SimpleStringProperty()

    val diagram: PlantUMLDiagram = PlantUMLDiagram(source)

    init {
        handleSourceUpdate(source)
    }

    fun handleSourceUpdate(newValue: String) {
        diagram.source = newValue
        image.value = diagram.image
        title.value = diagram.title

        svgContent.value = diagram.renderSVG()
        diagramService.updateDiagram(diagram)
    }
}