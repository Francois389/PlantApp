package com.fsp.plantapp.editor

import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.Text
import java.io.InputStream

@View
class EditorView(
    private val editorViewModel: EditorViewModel
) : IView {
    override fun createUI() = SplitPane(
        textArea,
        diagramImage
    )

    val textArea = VBox().apply {
        Label().apply {
            textProperty().bind(editorViewModel.diagramTitle)
        }.let(children::add)
        TextArea().apply {
            font = Font.font("Monospace")
            textProperty().bindBidirectional(editorViewModel.diagramSource)
        }.let(children::add)
    }

    val imageView: ImageView = ImageView()

    val diagramImage = VBox().apply {
        Text("Visualisation").let(children::add)
        imageView.let(children::add)
        imageView.imageProperty().bind(editorViewModel.image)
    }

}