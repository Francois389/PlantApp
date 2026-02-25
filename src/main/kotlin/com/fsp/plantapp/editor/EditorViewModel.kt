package com.fsp.plantapp.editor

import com.fsp.plantapp.diagram.DiagramService
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import net.sourceforge.plantuml.SourceStringReader
import org.w3c.dom.Element
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOMetadataNode
import javax.xml.parsers.DocumentBuilderFactory

class EditorViewModel(
    private val diagramService: DiagramService
) {
    val exportPaneVisible: Property<Boolean> = SimpleBooleanProperty(true)
    val errorText = SimpleStringProperty()

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