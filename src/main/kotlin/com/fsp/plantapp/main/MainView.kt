package com.fsp.plantapp.main

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import java.io.InputStream

fun ImageView.updateImage(newImage: InputStream) {
    this.image = javafx.scene.image.Image(newImage)
}

class MainView(viewModel: MainViewModel) : SplitPane() {
    init {
        val leftPane = VBox().apply {
            val titleLabel = Label().apply {
                textProperty().bindBidirectional(viewModel.diagramTitle)
            }
            val fileDestinationFields = TextField().apply {
                promptText = "Chemin du fichier exporté"
            }
            val pathExport = HBox(
                Label("Exporter vers :"),
                fileDestinationFields,
                Button("Exporter").apply {
                    setOnAction { viewModel.saveDiagramToFile(fileDestinationFields.text) }
                }
            )

            val textArea = TextArea().apply {
                textProperty().bindBidirectional(viewModel.textSource)
                prefRowCount = 10
            }
            children.addAll(
                titleLabel,
                textArea,
                Button("Générer le diagramme").apply {
                    setOnAction { viewModel.renderDiagram() }
                },
                pathExport,
                Label().apply {
                    textProperty().bindBidirectional(viewModel.errorText)
                    style = "-fx-text-fill: red;"
                }
            )
            VBox.setVgrow(textArea, Priority.ALWAYS)
            alignment = Pos.TOP_CENTER
        }

        val imageView = ImageView().apply {
            isPreserveRatio = true
            viewModel.imageOutput.addListener { _, _, newValue ->
                newValue?.let { updateImage(newValue) }
            }
            updateImage(viewModel.imageOutput.value)
        }

        items.addAll(
            leftPane,
            Pane().apply {
                children.add(imageView)
            }
        )
        setDividerPositions(0.3)
    }
}
