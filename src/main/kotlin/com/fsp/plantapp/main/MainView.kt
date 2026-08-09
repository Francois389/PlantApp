package com.fsp.plantapp.main

import com.fsp.plantapp.editor.EditorView
import com.fsp.plantapp.exportation.ExportView
import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import io.github.francois389.javaspringfx.navigation.Navigator
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

@View
class MainView(
    private val navigator: Navigator,
    private val viewModel: MainViewModel,
) : IView {


    override fun createUI() = TabPane().apply {
        tabs.addAll(
            Tab("Editor", navigator.findView(EditorView::class)).apply {
                isClosable = false
            },
            Tab("Export", navigator.findView(ExportView::class)).apply {
                isClosable = false
            }
        )
        selectionModel.select(0)
    }
}