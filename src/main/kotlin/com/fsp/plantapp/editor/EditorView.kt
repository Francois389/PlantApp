package com.fsp.plantapp.editor

import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font

@View
class EditorView(
    private val editorViewModel: EditorViewModel
) : IView {
    override fun createUI() = SplitPane(
        textArea,
        diagramImage
    ).apply {
        setDividerPositions(0.3)
    }

    val textArea = VBox().apply {
        Label().apply {
            textProperty().bind(editorViewModel.diagramTitle)
        }.let(children::add)
        TextArea().apply {
            font = Font.font("Monospace")
            textProperty().bindBidirectional(editorViewModel.diagramSource)
            VBox.setVgrow(this, Priority.ALWAYS)
        }.let(children::add)
        alignment = Pos.CENTER
    }

    val imageView: ImageView = ImageView()

    val diagramImage = VBox().apply imageParent@{
        minWidth = 0.0   // ← autorise la VBox à rétrécir
        minHeight = 0.0
        imageView.apply {
            imageProperty().bind(editorViewModel.image)
            isPreserveRatio = true

            // Bind la taille d'affichage sur le VBox parent
            fitWidthProperty().bind(this@imageParent.widthProperty().subtract(10.0))
            fitHeightProperty().bind(this@imageParent.heightProperty().subtract(10.0))
        }.let(children::add)
        alignment = Pos.CENTER
    }

}