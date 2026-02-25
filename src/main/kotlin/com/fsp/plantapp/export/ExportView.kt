package com.fsp.plantapp.export

import com.fsp.plantapp.Button
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser


class ExportView(viewModel: ExportViewModel) : VBox() {
    init {
        spacing = 10.0
        val horizontalPadding = 100.0
        padding = javafx.geometry.Insets(0.0, horizontalPadding, 0.0, horizontalPadding)
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
        val exporterBtn = Button("Exporter") {
            viewModel.exportDiagramm()
        }.apply {
            disableProperty().bind(viewModel.exportValid.not())
        }
        val errorText = Text().apply {
            textProperty().bind(viewModel.errorText)
            style = "-fx-fill: red;"
        }


        alignment = Pos.TOP_CENTER
        children.addAll(
            Text("Exportation du diagramme"),
            fileNameField,
            destinationDirectoryField,
            exporterBtn,
            errorText
        )
    }
}