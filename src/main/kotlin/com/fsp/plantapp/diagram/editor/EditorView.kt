package com.fsp.plantapp.editor

import com.fsp.plantapp.diagram.Diagram
import com.fsp.plantapp.editor.preview.PreviewView
import io.github.francois389.javaspringfx.navigation.IView
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font

class EditorView(
    private val diagram: Diagram
) : IView {
    private val editorViewModel: EditorViewModel = EditorViewModel(diagram)

    override fun createUI() = SplitPane(
        textArea,
        PreviewView(diagram).createUI()
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
}