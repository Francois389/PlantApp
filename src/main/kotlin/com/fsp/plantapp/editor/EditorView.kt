package com.fsp.plantapp.editor

import com.fsp.plantapp.Button
import com.fsp.plantapp.Navigator
import com.fsp.plantapp.Screen.ExportScreen
import javafx.application.Platform
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.control.ToggleButton
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.layout.VBox.setVgrow

fun ImageView.updateImage(newImage: ByteArray) {
    newImage.inputStream().use { stream ->
        this.image = javafx.scene.image.Image(stream)
    }
}

class EditorView(viewModel: EditorViewModel) : SplitPane() {
    init {
        orientation = Orientation.VERTICAL
        val editorSplitPane = SplitPane().apply {
            items.addAll(
                VBox().apply {
                    val textArea = TextArea().apply {
                        textProperty().bindBidirectional(viewModel.textSource)
                        prefRowCount = 10
                    }
                    setVgrow(textArea, Priority.ALWAYS)
                    alignment = Pos.TOP_CENTER
                    children.addAll(
                        Label().apply {
                            textProperty().bindBidirectional(viewModel.diagramTitle)
                        },
                        textArea,
                        HBox().apply {
                            children.addAll(
                                ToggleButton("Exporter...").apply {
                                    selectedProperty().bindBidirectional(viewModel.exportPaneVisible)
                                    setOnAction {
                                        println("Export button clicked, exportPaneVisible: ${viewModel.exportPaneVisible.value}")
                                    }
                                },
                                Button("Générer le diagramme") {
                                    viewModel.regenerateDiagram()
                                }
                            )
                        },
                    )
                },
                Pane(
                    ImageView().apply {
                        isPreserveRatio = true
                        viewModel.imageOutput.addListener { _, _, newValue ->
                            newValue?.let { updateImage(newValue) }
                        }
                        updateImage(viewModel.imageOutput.value)
                    }
                ),
            )
            setDividerPositions(0.3)
        }
        setVgrow(editorSplitPane, Priority.ALWAYS)

        val exportPane = Navigator.getScreen(ExportScreen).apply {
            isVisible = false
            isManaged = false
            managedProperty().bindBidirectional(viewModel.exportPaneVisible)
            visibleProperty().bindBidirectional(viewModel.exportPaneVisible)
        }

        viewModel.exportPaneVisible.addListener { _, _, newValue ->
            updateDividerPostion(newValue)
        }

        items.addAll(
            editorSplitPane,
            exportPane
        )
        Platform.runLater {
            updateDividerPostion(viewModel.exportPaneVisible.value)
        }
    }

    private fun updateDividerPostion(isExportPaneVisiable: Boolean) {
        if (isExportPaneVisiable) {
            setDividerPositions(0.7)
        } else {
            setDividerPositions(1.0)
        }
        // JavaFX ne met pas à jour les positions des dividers immédiatement.
        // En affichant les positions des dividers, on force JavaFX à les appliquer
        println("dividerPositions: ${dividerPositions.joinToString(", ")}")
    }
}
