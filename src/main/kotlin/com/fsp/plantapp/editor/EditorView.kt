package com.fsp.plantapp.editor

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.layout.VBox.setVgrow

fun ImageView.updateImage(newImage: ByteArray) {
    newImage.inputStream().use { stream ->
        this.image = Image(stream)
    }
}

class EditorView(viewModel: EditorViewModel) : SplitPane() {
    init {
        items.addAll(
            VBox().apply {
                val textArea = TextArea().apply {
                    prefRowCount = 10
                    text = viewModel.source
                    textProperty().subscribe(viewModel::handleSourceUpdate)
                }
                setVgrow(textArea, Priority.ALWAYS)
                alignment = Pos.TOP_CENTER
                children.addAll(
                    Label().apply {
                        textProperty().bind(viewModel.title)
                    },
                    textArea,
                )
            },
            ScrollPane(
                ImageView().apply {
                    isPreserveRatio = true
                    viewModel.image.subscribe { newImage -> newImage?.let { updateImage(newImage) } }
                    updateImage(viewModel.image.value)

                    setOnScroll { event ->
                        if (event.isControlDown && event.deltaY != 0.0) {
                            val zoomFactor = if (event.deltaY > 0) 1.1 else 1.0 / 1.1
                            fitWidth = (fitWidth * zoomFactor).coerceIn(50.0, 5000.0)
                            fitHeight = (fitHeight * zoomFactor).coerceIn(50.0, 5000.0)
                            event.consume()
                        }
                    }
                }
            ).apply {
                isPannable = true
            },
        )
    }
}
