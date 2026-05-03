package com.fsp.plantapp.export

import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import javafx.scene.Parent
import javafx.scene.layout.VBox
import javafx.scene.text.Text

@View
class ExportView: IView {
    override fun createUI(): Parent {
        return VBox().apply {
            Text("Export").apply {
                style = "-fx-font-size: 24px;"
            }.let(children::add)
        }
    }
}