package com.fsp.plantapp.export

import com.fsp.plantapp.diagram.DiagramService
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import org.w3c.dom.Element
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOMetadataNode
import javax.xml.parsers.DocumentBuilderFactory

class ExportViewModel(
    private val exportService: DiagramService
) {
    val fileName = SimpleStringProperty("")
    val directoryDestination = SimpleStringProperty("")
    val errorText = SimpleStringProperty("")
    val exportValid = SimpleBooleanProperty(false)
    val successText = SimpleStringProperty("")


    init {
        fileName.value = exportService.getDiagram().diagrammTitle
        fileName.addListener { _, _, newValue ->
            updateFeedbackText()
        }
        directoryDestination.addListener { _, _, newValue ->
            updateFeedbackText()
        }
        updateFeedbackText()
    }

    private fun updateFeedbackText() {
        successText.value = ""
        if (fileName.value.isEmpty()) {
            exportValid.value = false
            errorText.value = "Erreur : Le nom du fichier ne peut pas être vide."
        } else if (directoryDestination.value.isEmpty()) {
            exportValid.value = false
            errorText.value = "Erreur : Le répertoire de destination ne peut pas être vide."
        } else if (checkIfFileExists()) {
            exportValid.value = false
            errorText.value = "Erreur : Un fichier avec le même nom existe déjà à cet emplacement."
        } else {
            exportValid.value = true
            errorText.value = ""
        }
    }

    fun detectTitleFromSource() {
        fileName.value = exportService.getDiagram().diagrammTitle
    }

    fun saveDiagramToFile(path: String) {
        val diagramm = exportService.getDiagram()
        try {
            // Lire le PNG depuis les bytes
            val bufferedImage = ImageIO.read(diagramm.diagrammImage.inputStream())

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
            textEntry.setAttribute("value", diagramm.diagrammSource)
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
            successText.value = "Diagramme exporté avec succès à : $path"
        } catch (e: Exception) {
            errorText.value = "Erreur lors de la sauvegarde : ${e.message}"
            println(e.message)
        }
    }

    fun checkIfFileExists(): Boolean {
        val diagramFile = getDiagramFile()
        return diagramFile.exists()
    }

    private fun getDiagramFile(): File = File("${directoryDestination.value}/${fileName.value}.png")

    fun exportDiagramm() {
        if (!checkIfFileExists()) {
            saveDiagramToFile(getDiagramFile().path)
        } else {
            errorText.value = "Erreur : Un fichier avec le même nom existe déjà à cet emplacement."
        }
    }
}
