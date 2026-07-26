package com.fsp.plantapp.export

import com.fsp.plantapp.diagram.DiagramService
import com.fsp.plantapp.util.capitalize
import com.fsp.plantapp.util.removeAccent
import io.github.francois389.javaspringfx.annotations.ViewModel
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.StringBinding
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
    val successText = SimpleStringProperty("")
    val formatSelected = SimpleObjectProperty<NameFormat>(NameFormat.Default)
    val overwriteExistingFile = SimpleBooleanProperty(false)

    /** Relais pour fournir à JavaFX une valeur à observer */
    private val fileExistsOnDisk = SimpleBooleanProperty(false)

    val fileNameFormat: StringBinding = Bindings.createStringBinding(
        { formatSelected.value.formatteur(fileNameInput.value) },
        formatSelected, fileNameInput
    )
    val entreManquante: BooleanBinding = Bindings.createBooleanBinding(
        { fileNameInput.value.isEmpty() || directoryDestination.value.isEmpty() },
        fileNameInput, directoryDestination
    )
    val alreadyExistingFile: BooleanBinding = Bindings.createBooleanBinding(
        { fileExistsOnDisk.value && !entreManquante.value },
        fileExistsOnDisk, entreManquante
    )
    val erreurs: ObjectBinding<Set<Erreur>> = Bindings.createObjectBinding<Set<Erreur>>(
        {
            setOfNotNull(
                if (fileNameInput.value.isEmpty()) Erreur.FileNameEmpty else null,
                if (directoryDestination.value.isEmpty()) Erreur.DirectoryEmpty else null,
                if (alreadyExistingFile.value) Erreur.FileExistOnDisk else null,
            )
        },
        fileNameInput, directoryDestination, alreadyExistingFile
    )

    init {
        detectTitleFromSource()
        refreshFileExistsOnDisk()

        fileNameFormat.addListener {
            successText.value = ""
            refreshFileExistsOnDisk()
        }
        directoryDestination.addListener {
            successText.value = ""
            refreshFileExistsOnDisk()
        }
        diagramService.addObserver {
            detectTitleFromSource()
        }
    }

    fun refreshFileExistsOnDisk() {
        fileExistsOnDisk.value = diagramFile.exists()
    }

    fun detectTitleFromSource() {
        fileNameInput.value = diagramService.title
    }

    fun exportDiagramm() {
        if (diagramFile.exists() && overwriteExistingFile.value.not()) {
            refreshFileExistsOnDisk()
        } else {
            saveDiagramToFile(diagramFile.path)
        }
    }

    private val diagramFile: File
        get() = File("${directoryDestination.value}/${fileNameFormat.value}.png")

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

                successText.value = "Diagramme exporté avec succès à : $path"
            } else {
                successText.value = ""
                println(
                    "Une erreur est survenue lors de la génération du diagramme." +
                            " Vérifier que le diagramme est correctement généré."
                )
            }
        } catch (e: Exception) {
            successText.value = ""
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

    sealed interface Erreur {
        object FileNameEmpty : Erreur
        object DirectoryEmpty : Erreur
        object FileExistOnDisk : Erreur

    }
}