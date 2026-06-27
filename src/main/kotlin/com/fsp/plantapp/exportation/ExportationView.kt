package com.fsp.plantapp.exportation

import com.fsp.plantapp.export.ExportViewModel
import com.fsp.plantapp.util.Button
import com.fsp.plantapp.util.Insets
import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser

@View
class ExportationView(
    private val viewModel: ExportViewModel
) : IView {
    override fun createUI() = VBox().apply {
        spacing = 10.0
        padding = Insets(100.0)
        alignment = Pos.TOP_CENTER
        children.addAll(
            Text("Exportation du diagramme"),
            fileNameField,
            destinationDirectoryField,
            exporterBtn,
            errorText,
            successText,
        )
    }

    val fileNameField = HBox().apply {
        spacing = 5.0
        alignment = Pos.CENTER_LEFT
        children.addAll(
            Label("Nom du fichier :"),
            TextField().apply {
                textProperty().bindBidirectional(viewModel.fileName)
                promptText = "DiagSequence"
                HBox.setHgrow(this, Priority.ALWAYS)
            },
            Button("Detecter") {
                viewModel.detectTitleFromSource()
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
            },
            Button("Parcourir...") {
                val directorieChooser = DirectoryChooser()
                directorieChooser.title = "Sélectionner le dossier où exporter les tâches"

                val selectedFile = directorieChooser.showDialog(this.scene.window)
                selectedFile?.let {
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

        CheckBox("Écraser le fichier existant").apply {
            selectedProperty().bindBidirectional(viewModel.overwriteExistingFile)
            visibleProperty().bind(viewModel.alreadyExistingFile)
            managedProperty().bind(visibleProperty())
        }.let(children::add)

    }
    val errorText = Text().apply {
        textProperty().bind(viewModel.errorText)
        style = "-fx-fill: red;"
    }
    val successText = Text().apply {
        textProperty().bind(viewModel.successText)
        style = "-fx-fill: green;"
        // TODO add fading animation to make it disappear after a few seconds
    }
}