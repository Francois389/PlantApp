package com.fsp.plantapp.exportation

import com.fsp.plantapp.export.ExportViewModel
import com.fsp.plantapp.export.ExportViewModel.NameFormat
import com.fsp.plantapp.util.Button
import com.fsp.plantapp.util.Insets
import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import javafx.util.StringConverter
import java.io.File

@View
class ExportationView(
    private val viewModel: ExportViewModel
) : IView {

    init {
        viewModel.erreurs.addListener { _, _, erreurs ->
            updateErreur(erreurs)
        }
    }

    override fun createUI() = VBox().apply {
        updateErreur(viewModel.erreurs.value)
        spacing = 10.0
        padding = Insets(100.0)
        alignment = Pos.TOP_CENTER
        children.addAll(
            Text("Exportation du diagramme"),
            fileNameField,
            fileNameForamtteField,
            destinationDirectoryField,
            exporterBtn,
            errorText,
            successText,
        )
    }

    val formatSelection = ChoiceBox<NameFormat>().apply {
        items.setAll(NameFormat.entries)
        valueProperty().bindBidirectional(viewModel.formatSelected)
        converter = object : StringConverter<NameFormat>() {

            val map = mapOf(
                NameFormat.Default to "Défaut",
                NameFormat.SansAccent to "Sans accent",
                NameFormat.CamelCase to "CamelCase",
                NameFormat.SnakeCase to "snake_case"
            )

            override fun toString(format: NameFormat?): String? = map[format]

            override fun fromString(string: String?): NameFormat = map
                .entries
                .first { it.value == string }
                .key
        }

        val arrowKeys = setOf(KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.DOWN)

        addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            if (event.code in arrowKeys) {
                event.consume()
                val currentIndex = selectionModel.selectedIndex
                val increment = when (event.code) {
                    KeyCode.UP, KeyCode.LEFT -> -1
                    KeyCode.DOWN, KeyCode.RIGHT -> 1
                    else -> 0
                }
                if (items.isNotEmpty()) {
                    selectionModel.select(
                        ((currentIndex + increment + items.size) % items.size)
                            .coerceIn(0, items.size - 1)
                    )
                }
            }
        }

        addEventFilter(KeyEvent.KEY_RELEASED) { event ->
            if (event.code in arrowKeys) {
                event.consume()
            }
        }
    }

    val fileNameField = HBox().apply {
        spacing = 5.0
        alignment = Pos.CENTER_LEFT
        children.addAll(
            Label("Nom du fichier :"),
            TextField().apply {
                textProperty().bindBidirectional(viewModel.fileNameInput)
                promptText = "DiagSequence"
                HBox.setHgrow(this, Priority.ALWAYS)
                styleProperty().bind(
                    Bindings.createStringBinding(
                        {
                            if (viewModel.erreurs.value.contains(ExportViewModel.Erreur.FileNameEmpty)) {
                                "-fx-border-color: red;"
                            } else ""
                        },
                        viewModel.erreurs
                    )
                )
            },
            Button("Detecter") {
                viewModel.detectTitleFromSource()
            }
        )
    }

    val fileNameForamtteField = HBox().apply {
        spacing = 5.0
        alignment = Pos.CENTER_LEFT
        children.addAll(
            formatSelection,
            Label("Nom du fichier formatté :"),
            TextField().apply {
                isEditable = false
                textProperty().bind(viewModel.fileNameFormat)
                HBox.setHgrow(this, Priority.ALWAYS)
            }
        )
    }

    val destinationDirectoryField = HBox().apply {
        spacing = 5.0
        alignment = Pos.CENTER_LEFT
        children.addAll(
            Label("Dossier de destination :"),
            TextField().apply {
                textProperty().bindBidirectional(viewModel.directoryDestination)
                promptText = "/home/john/diagram"
                HBox.setHgrow(this, Priority.ALWAYS)

                styleProperty().bind(
                    Bindings.createStringBinding(
                        {
                            if (viewModel.erreurs.value.contains(ExportViewModel.Erreur.DirectoryEmpty)) {
                                "-fx-border-color: red;"
                            } else ""
                        },
                        viewModel.erreurs
                    )
                )
            },
            Button("Parcourir...") {
                val directorieChooser = DirectoryChooser()
                viewModel.directoryDestination
                    .takeIf { it.value.isNotEmpty() }
                    ?.let { directorieChooser.initialDirectory = File(it.value) }
                directorieChooser.title = "Sélectionner le dossier où exporter le diagramme"

                directorieChooser.showDialog(this.scene.window)?.let {
                    viewModel.directoryDestination.value = it.absolutePath
                }
            }
        )
    }
    val exporterBtn = VBox().apply {
        spacing = 5.0
        alignment = Pos.CENTER

        Button("Exporter") {
            viewModel.exportDiagramm()
        }.apply {
            disableProperty().bind(
                // Disable the button if there is missing input or (if the file already exists and overwrite is not checked)
                viewModel.let {
                    it.entreManquante
                        .or(
                            it.alreadyExistingFile
                                .and(it.overwriteExistingFile.not())
                        )
                }
            )
        }.let(children::add)

        HBox().apply {
            spacing = 5.0
            alignment = Pos.CENTER

            visibleProperty().bind(viewModel.alreadyExistingFile)
            managedProperty().bind(visibleProperty())

            children.addAll(
                CheckBox("Écraser le fichier existant").apply {
                    selectedProperty().bindBidirectional(viewModel.overwriteExistingFile)
                },
                Button("Rafraichir", viewModel::refreshFileExistsOnDisk)
            )
        }.let(children::add)
    }
    val errorText = Text().apply { style = "-fx-fill: red;" }

    val successText = Text().apply {
        textProperty().bind(viewModel.successText)
        style = "-fx-fill: green;"
        // TODO add fading animation to make it disappear after a few seconds
    }

    fun updateErreur(erreurs: Set<ExportViewModel.Erreur>) {
        val message = erreurs.joinToString("\n") {
            when (it) {
                ExportViewModel.Erreur.FileNameEmpty -> "Le nom du fichier ne peut pas être vide."
                ExportViewModel.Erreur.DirectoryEmpty -> "Le répertoire de destination ne peut pas être vide."
                ExportViewModel.Erreur.FileExistOnDisk -> "Un fichier avec le même nom existe déjà à cet emplacement."
            }
        }
        errorText.text = message
        errorText.isVisible = erreurs.isNotEmpty()
    }
}