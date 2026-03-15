package com.fsp.plantapp.editor

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
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
            Pane(
                ImageView().apply {
                    isPreserveRatio = true
                    viewModel.image.subscribe { newImage -> newImage?.let { updateImage(newImage) } }
                    updateImage(viewModel.image.value)
                }
            ),
        )
    }
}
