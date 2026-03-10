package com.fsp.plantapp.editor

import com.fsp.plantapp.diagram.DiagramService
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class EditorViewModel(
    private val diagramService: DiagramService
) {
    val diagramSourceText = SimpleStringProperty()
    val imageOutput: Property<ByteArray> = SimpleObjectProperty()
    val diagramTitle = SimpleStringProperty()

    init {
        diagramSourceText.addListener { _, _, _ ->
            diagramService.setDiagramSource(diagramSourceText.value)
            diagramService.getDiagram().let {
                imageOutput.value = it.diagrammImage
                diagramTitle.value = it.diagrammTitle
                diagramSourceText.value = it.diagrammSource
            }
        }

        diagramService.getDiagram().let {
            imageOutput.value = it.diagrammImage
            diagramTitle.value = it.diagrammTitle
            diagramSourceText.value = it.diagrammSource
        }
    }

    fun regenerateDiagram() {
        diagramService.regenerateDiagramImage()
    }
}