package com.fsp.plantapp.main

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import java.io.File
import javax.imageio.IIOImage
import org.w3c.dom.Element
import javax.imageio.metadata.IIOMetadataNode
import javax.xml.parsers.DocumentBuilderFactory

class MainViewModel {

    val textSource = SimpleStringProperty(
        """
        @startuml;
        title Titre
        Alice -> Bob: Hello
        Bob -> Alice: Hi!
        @enduml
    """.trimIndent()
    )
    val imageOutput: Property<InputStream> = SimpleObjectProperty()
    val errorText = SimpleStringProperty()
    val diagramTitle = SimpleStringProperty()

    init {
        textSource.addListener { _, _, _ ->
            renderDiagram()
            updateTitle()
        }
        renderDiagram()
        updateTitle()
    }

    private fun updateTitle() {
        diagramTitle.value = textSource.value
            .split("\n")
            .first { it.contains("title") }
            .substringAfter("title")
            .trim()
    }

    fun renderDiagram() {
        try {
            val source = textSource.value
            val reader = SourceStringReader(source)

            val outputStream = ByteArrayOutputStream()
            reader.outputImage(outputStream)

            val inputStream = outputStream.toByteArray().inputStream()
            imageOutput.value = inputStream
            errorText.value = ""
        } catch (e: Exception) {
            errorText.value = e.message
        }
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