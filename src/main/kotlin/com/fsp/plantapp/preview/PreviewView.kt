package com.fsp.plantapp.preview

import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.VBox
import org.girod.javafx.svgimage.SVGLoader

@View
class PreviewView(
    private val previewViewModel: PreviewViewModel,
) : IView {
    override fun createUI(): Parent = VBox().apply {
        alignment = Pos.CENTER

        previewViewModel.svgDiagram.addListener { _, _, newValue ->
            updateDiagram(newValue)
        }
        updateDiagram(previewViewModel.svgDiagram.value)
    }

    private fun VBox.updateDiagram(newValue: String?) {
        children.clear()
        val svgImage = SVGLoader.load(newValue)
        children.add(svgImage)
    }
}