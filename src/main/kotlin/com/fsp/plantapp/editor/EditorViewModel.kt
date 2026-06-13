package com.fsp.plantapp.editor

import com.fsp.plantapp.diagram.DiagramService
import io.github.francois389.javaspringfx.annotations.ViewModel
import javafx.beans.property.SimpleStringProperty

@ViewModel
class EditorViewModel(
    private val diagramService: DiagramService
) {

    val initialSource = diagramService.diagramSource

    val diagramTitle = SimpleStringProperty(diagramService.title)
    val diagramSource = SimpleStringProperty(initialSource)

    init {
        diagramSource.addListener { _, _, newValue ->
            newValue?.let {
                diagramService.diagramSource = newValue
            }
        }
        diagramService.addObserver {
            diagramTitle.value = diagramService.title
        }
    }
}