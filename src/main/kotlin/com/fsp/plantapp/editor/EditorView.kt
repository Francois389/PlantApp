package com.fsp.plantapp.editor

import com.fsp.plantapp.Button
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
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
        items.addAll(
            VBox().apply {
                val textArea = TextArea().apply {
                    textProperty().bindBidirectional(viewModel.diagramSourceText)
                    prefRowCount = 10
                }
                setVgrow(textArea, Priority.ALWAYS)
                alignment = Pos.TOP_CENTER
                children.addAll(
                    Label().apply {
                        textProperty().bindBidirectional(viewModel.diagramTitle)
                    },
                    textArea,
                    Button("Générer le diagramme") {
                        viewModel.regenerateDiagram()
                    }.apply {
                        // C'est un problème que je ne comprends pas.
                        // Quand `viewModel.regenerateDiagram()` est défini dans l'onAction, tous va bien.
                        // Mais dès que je le retire, l'event listener ne se déclenche plus.
                        isManaged = false
                        isVisible = false
                    }
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
}
