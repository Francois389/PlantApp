package com.fsp.plantapp.export

import com.fsp.plantapp.diagram.DiagramService
import com.fsp.plantapp.util.capitalize
import com.fsp.plantapp.util.removeAccent
import io.github.francois389.javaspringfx.annotations.ViewModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.w3c.dom.Element
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale.getDefault
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOMetadataNode
import javax.xml.parsers.DocumentBuilderFactory

@ViewModel
class ExportViewModel(
    private val diagramService: DiagramService
) {
    val fileNameInput = SimpleStringProperty("")
    val directoryDestination = SimpleStringProperty("")
    val errorText = SimpleStringProperty("")
    val successText = SimpleStringProperty("")
    val fileNameFormat = SimpleStringProperty("")
    val formatSelected = SimpleObjectProperty<NameFormat>(NameFormat.Default)

    val overwriteExistingFile = SimpleBooleanProperty(false)
    val alreadyExistingFile = SimpleBooleanProperty(false)
    val entreManquante = SimpleBooleanProperty(false)


    init {
        detectTitleFromSource()
        updateFeedbackText()

        formatSelected.addListener { _, _, _ ->
            updateFileNameFormat()
        }
        fileNameInput.addListener { _, _, _ ->
            updateFeedbackText()
            updateFileNameFormat()
        }
        directoryDestination.addListener { _, _, _ ->
            updateFeedbackText()
        }
        diagramService.addObserver {
            detectTitleFromSource()
        }
    }

    private fun updateFileNameFormat() {
        fileNameFormat.value = formatSelected.value.formatteur(fileNameInput.value)
    }

    private fun updateFeedbackText() {
        successText.value = ""
        overwriteExistingFile.value = false
        when {
            fileNameInput.value.isEmpty() -> {
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
        fileNameInput.value = diagramService.title
    }

    fun exportDiagramm() {
        if (diagramFile.exists() && overwriteExistingFile.value.not()) {
            errorText.value = "Erreur : Un fichier avec le même nom existe déjà à cet emplacement."
        } else {
            saveDiagramToFile(diagramFile.path)
        }
    }

    private val diagramFile: File
        get() = File("${directoryDestination.value}/${fileNameInput.value}.png")

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

    enum class NameFormat(
        val formatteur: (name: String) -> String
    ) {
        Default({ it }),
        SansAccent({ name ->
            name.removeAccent()
        }),
        CamelCase({ name ->
            name
                .split(" ")
                .joinToString(separator = "") {
                    it
                        .removeAccent()
                        .filter(Char::isJavaIdentifierPart)
                        .capitalize()
                }
        }),
        SnakeCase({ name ->
            name
                .split(" ")
                .map {
                    it
                        .removeAccent()
                        .filter(Char::isJavaIdentifierPart)
                        .lowercase(getDefault())
                        .trim()
                }
                .filterNot { it.isBlank() }
                .joinToString(separator = "_")
        })
        ;
    }
}
