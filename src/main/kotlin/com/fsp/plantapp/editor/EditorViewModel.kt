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
    val exportPaneVisible: Property<Boolean> = SimpleBooleanProperty(false)
    val textSource = SimpleStringProperty(
        """
        @startuml;
        title Titre
        Alice -> Bob: Hello
        Bob -> Alice: Hi!
        @enduml
    """.trimIndent()
    )
    val imageOutput: Property<ByteArray> = SimpleObjectProperty()
    val errorText = SimpleStringProperty()
    val diagramTitle = SimpleStringProperty()

    init {
        textSource.addListener { _, _, _ ->
            diagramService.setDiagramSource(textSource.value)
            diagramService.getDiagram().let {
                imageOutput.value = it.diagrammImage
                diagramTitle.value = it.diagrammTitle
                textSource.value = it.diagrammSource
            }
        }
        diagramService.getDiagram().let {
            imageOutput.value = it.diagrammImage
            diagramTitle.value = it.diagrammTitle
            textSource.value = it.diagrammSource
        }
    }

    fun regenerateDiagram() {
        diagramService.regenerateDiagramImage()
    }


    fun saveDiagramToFile(path: String) {
        try {
            // Re-générer un InputStream frais (celui de imageOutput a peut-être déjà été lu)
            val source = textSource.value
            val reader = SourceStringReader(source)
            val outputStream = ByteArrayOutputStream()
            reader.outputImage(outputStream)
            val imageBytes = outputStream.toByteArray()

            // Lire le PNG depuis les bytes
            val bufferedImage = ImageIO.read(imageBytes.inputStream())

            // Préparer le writer PNG
            val writer = ImageIO.getImageWritersByFormatName("png").next()
            val writeParam = writer.defaultWriteParam
            val typeSpecifier = ImageTypeSpecifier.createFromRenderedImage(bufferedImage)
            val metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam)

            // Injecter le source PlantUML dans un chunk tEXt
            val metaFormat = "javax_imageio_png_1.0"
            val root = metadata.getAsTree(metaFormat) as Element
            val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
            val textNode = IIOMetadataNode("tEXt")
            val textEntry = IIOMetadataNode("tEXtEntry")
            textEntry.setAttribute("keyword", "plantuml_source")
            textEntry.setAttribute("value", source)
            textNode.appendChild(textEntry)
            root.appendChild(textNode)
            metadata.setFromTree(metaFormat, root)

            // Écrire le fichier
            val outputFile = File(path)
            val imageOutputStream = ImageIO.createImageOutputStream(outputFile)
            writer.output = imageOutputStream
            writer.write(metadata, IIOImage(bufferedImage, null, metadata), writeParam)
            imageOutputStream.close()
            writer.dispose()

            errorText.value = ""
        } catch (e: Exception) {
            errorText.value = "Erreur lors de la sauvegarde : ${e.message}"
            println(e.message)
        }
    }
}