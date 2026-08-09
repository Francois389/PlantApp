package com.fsp.plantapp.editor

import com.fsp.plantapp.diagram.Diagram
import io.github.francois389.javaspringfx.annotations.ViewModel
import javafx.beans.property.SimpleStringProperty

class EditorViewModel(
    private val diagram: Diagram
) {

    val initialSource = diagram.diagramSource

    val diagramTitle = SimpleStringProperty(diagram.title)
    val diagramSource = SimpleStringProperty(initialSource)

    init {
        diagramSource.addListener { _, _, newValue ->
            newValue?.let {
                diagram.diagramSource = newValue
            }
        }
        diagram.addObserver {
            diagramTitle.value = diagram.title
        }
    }
}