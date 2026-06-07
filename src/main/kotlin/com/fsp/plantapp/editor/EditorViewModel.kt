package com.fsp.plantapp.editor

import com.fsp.plantapp.diagram.DiagramService
import io.github.francois389.javaspringfx.annotations.ViewModel
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@ViewModel
class EditorViewModel(
    private val diagramService: DiagramService
) {

    val initialSource = """
        @startuml;
        title Titre
        Alice -> Bob: Hello
        Bob -> Alice: Hi!
        @enduml
    """.trimIndent()
    val diagramTitle = SimpleStringProperty()
    val diagramSource = SimpleStringProperty(initialSource)
    val image = SimpleObjectProperty<Image>()

    init {
        updateTitle(initialSource)
        updateImage(initialSource)
        diagramSource.addListener { _, _, newValue ->
            newValue?.let {
                updateTitle(newValue)
                updateImage(newValue)
            }
        }
    }

    private fun updateImage(source: String) {
        val tempOut = ByteArrayOutputStream()
        val isSuccess = diagramService.renderDiagram(source, tempOut)
        if (isSuccess) {
            image.set(Image(ByteArrayInputStream(tempOut.toByteArray())))
        }
        tempOut.close()
    }

    private fun updateTitle(souce: String) {
        souce
            .split("\n")
            .firstOrNull { it.startsWith("title ") }
            ?.substringAfter("title ")
            ?.trim()
            ?.let(diagramTitle::set)
    }
}