package com.fsp.plantapp.editor

import com.fsp.plantapp.Button
import com.fsp.plantapp.Navigator
import com.fsp.plantapp.Screen
import com.fsp.plantapp.Screen.ExportScreen
import com.fsp.plantapp.diagram.DiagramService
import com.fsp.plantapp.export.ExportView
import com.fsp.plantapp.export.ExportViewModel
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

fun ImageView.updateImage(newImage: ByteArray) {
    newImage.inputStream().use { stream ->
        this.image = javafx.scene.image.Image(stream)
    }
}

class EditorView(viewModel: EditorViewModel) : VBox() {
    init {
        val splitPane = SplitPane().apply {
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
                                Button("Exporter...") {
                                    viewModel.goToExport()
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
        setVgrow(splitPane, Priority.ALWAYS)

        val exportPane = Navigator.getScreen(ExportScreen).apply {
            isVisible = false
            isManaged = false
            managedProperty().bindBidirectional(viewModel.exportPaneVisible)
            visibleProperty().bindBidirectional(viewModel.exportPaneVisible)
        }

        children.addAll(
            splitPane,
            exportPane
        )
    }
}
