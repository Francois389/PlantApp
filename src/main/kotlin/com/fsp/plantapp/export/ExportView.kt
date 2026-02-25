package com.fsp.plantapp.export

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import com.fsp.plantapp.Button
import javafx.stage.DirectoryChooser


class ExportView(viewModel : ExportViewModel) : VBox() {
    init {
        val fileNameField = HBox().apply {
            spacing = 5.0
            alignment = Pos.CENTER_LEFT
            children.addAll(
                Label("Nom du fichier :"),
                TextField().apply {
                    promptText = "DiagSequence"
                }
            )
        }
        val destinationDirectoryField = HBox().apply {
            spacing = 5.0
            alignment = Pos.CENTER_LEFT
            children.addAll(
                Label("Dossier de destination :"),
                TextField().apply {
                    promptText = "C:/Users/JohnDoe/Desktop"
                },
                Button("Parcourir...") {
                    val directorieChooser = DirectoryChooser()
                    directorieChooser.title = "Sélectionner le dossier où exporter les tâches"

                    val selectedFile = directorieChooser.showDialog(this.scene.window)
                    selectedFile?.let {
                        it.absolutePath
                    }
                }
            )
        }


        alignment = Pos.TOP_CENTER
        children.addAll(
            Text("Exportation du diagramme"),
            fileNameField,
            destinationDirectoryField,
        )
    }
}