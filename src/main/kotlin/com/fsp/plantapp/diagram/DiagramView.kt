package com.fsp.plantapp

import com.fsp.plantapp.diagram.Diagram
import com.fsp.plantapp.editor.EditorView
import com.fsp.plantapp.exportation.ExportView
import io.github.francois389.javaspringfx.navigation.IView
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

class DiagramView(
    private val configuration: Configuration,
    private val diagram: Diagram
) : IView {
    override fun createUI(): Parent = TabPane().apply {
        tabs.addAll(
            Tab("Editeur", EditorView(diagram).createUI()).apply {
                isClosable = false
            },
            Tab("Exportation", ExportView(configuration, diagram).createUI()).apply {
                isClosable = false
            }
        )
        selectionModel.select(0)
    }
}