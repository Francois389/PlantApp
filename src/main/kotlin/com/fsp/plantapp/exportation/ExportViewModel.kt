package com.fsp.plantapp.export

import com.fsp.plantapp.diagram.DiagramService
import io.github.francois389.javaspringfx.annotations.ViewModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import org.w3c.dom.Element
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOMetadataNode
import javax.xml.parsers.DocumentBuilderFactory

@ViewModel
class ExportViewModel(
    private val diagramService: DiagramService
) {
    val fileName = SimpleStringProperty("")
    val directoryDestination = SimpleStringProperty("")
    val errorText = SimpleStringProperty("")
    val successText = SimpleStringProperty("")

    val overwriteExistingFile = SimpleBooleanProperty(false)
    val alreadyExistingFile = SimpleBooleanProperty(false)
    val entreManquante = SimpleBooleanProperty(false)


    init {
        detectTitleFromSource()
        updateFeedbackText()

        fileName.addListener { _, _, _ ->
            updateFeedbackText()
        }
        directoryDestination.addListener { _, _, _ ->
            updateFeedbackText()
        }
        diagramService.addObserver {
            detectTitleFromSource()
        }
    }

    private fun updateFeedbackText() {
        successText.value = ""
        overwriteExistingFile.value = false
        when {
            fileName.value.isEmpty() -> {
                entreManquante.value = false
                errorText.value = "Erreur : Le nom du fichier ne peut pas être vide."
            }

            directoryDestination.value.isEmpty() -> {
                entreManquante.value = false
                errorText.value = "Erreur : Le répertoire de destination ne peut pas être vide."
            }

            diagramFile.exists() -> {
                alreadyExistingFile.value = true
                errorText.value = "Erreur : Un fichier avec le même nom existe déjà à cet emplacement."
            }

            else -> {
                alreadyExistingFile.value = false
                entreManquante.value = false
                errorText.value = ""
            }
        }
    }

    fun detectTitleFromSource() {
        fileName.value = diagramService.title
    }

    fun exportDiagramm() {
        if (diagramFile.exists() && overwriteExistingFile.value.not()) {
            errorText.value = "Erreur : Un fichier avec le même nom existe déjà à cet emplacement."
        } else {
            saveDiagramToFile(diagramFile.path)
        }
    }

    private val diagramFile: File
        get() = File("${directoryDestination.value}/${fileName.value}.png")

    private fun saveDiagramToFile(path: String) {
        val diagramSource = diagramService.diagramSource
        try {
            // Lire le PNG depuis les bytes
            val tempOut = ByteArrayOutputStream()
            val isSuccess = diagramService.renderDiagram(tempOut)
            tempOut.close()

            if (isSuccess) {
                val bufferedImage = ImageIO.read(ByteArrayInputStream(tempOut.toByteArray()))

                writeImageToFile(bufferedImage, path, diagramSource)

                errorText.value = ""
                successText.value = "Diagramme exporté avec succès à : $path"
            } else {
                errorText.value = "Une erreur est survenue lors de la génération du diagramme." +
                        "\n Vérifier que le diagramme est correctement généré."
                successText.value = ""
            }
        } catch (e: Exception) {
            errorText.value = "Erreur lors de la sauvegarde : ${e.message}"
            println(e.message)
        }
    }

    private fun writeImageToFile(
        bufferedImage: BufferedImage?,
        path: String,
        diagramSource: String
    ) {
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
        textEntry.setAttribute("value", diagramSource)
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
    }
}
