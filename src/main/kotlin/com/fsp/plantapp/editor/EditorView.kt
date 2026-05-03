package com.fsp.plantapp.editor

import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import javafx.scene.layout.VBox
import javafx.scene.text.Text

@View
class EditorView : IView {
    override fun createUI() = VBox().apply {
        Text("Editor").apply {
            style = "-fx-font-size: 24px;"
        }.let(children::add)
    }
}