package com.fsp.plantapp.editor.preview

import com.fsp.plantapp.ZoomControl
import com.fsp.plantapp.util.estPositif
import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.girod.javafx.svgimage.SVGImage
import org.girod.javafx.svgimage.SVGLoader

@View
class PreviewView(
    private val previewViewModel: PreviewViewModel,
) : IView {

    val svgImageProperty = SimpleObjectProperty<SVGImage?>(null)
    val contentWrapper = StackPane().apply {
        alignment = Pos.CENTER
        svgImageProperty.addListener { _, _, newValue ->
            children.setAll(newValue)
        }
    }

    val previewParent = ScrollPane().apply {
        isPannable = true
        isFitToHeight = true
        isFitToWidth = true
        content = contentWrapper

        addEventFilter(ScrollEvent.SCROLL) { event ->
            if (event.isControlDown) {
                val deltaY = event.deltaY
                if (deltaY != 0.0) {
                    val plus = 1.1
                    val moins = 1 / 1.1
                    val zoomFactor = if (deltaY.estPositif()) plus else moins
                    previewViewModel.zoomLevel.value *= zoomFactor
                    event.consume()
                }
            }
        }
    }

    val warningLabels = VBox().apply {
        alignment = Pos.CENTER
        children.addAll(
            Label("L'agrandissement d'un diagramme trop petit peut ne pas apparaitre correctement."),
            Label("Gardez en considération que l'image finale est créée à la taille par défaut."),
        )
    }

    override fun createUI(): Parent = VBox().apply {
        alignment = Pos.TOP_CENTER
        children.addAll(
            previewViewModel.zoomLevel.let {
                ZoomControl(
                    reset = { it.set(1.0) },
                    plus = { it.value += 0.1 },
                    moins = { it.value -= 0.1 },
                    label = it.multiply(100).asString("%.0f%%")
                )
            },
            previewParent,
            warningLabels,
        )
        VBox.setVgrow(previewParent, Priority.ALWAYS)
        updateDiagram(previewViewModel.svgDiagram.value)
    }

    init {
        previewViewModel.svgDiagram.addListener { _, _, newValue ->
            previewViewModel.zoomLevel.set(1.0)
            updateDiagram(newValue)
        }
        previewViewModel.zoomLevel.addListener { _, oldZoom, newZoom ->
            updateDiagram(previewViewModel.svgDiagram.value)
        }
    }

    private fun updateDiagram(newValue: String?) {
        val svgImage = SVGLoader.load(newValue)
            .apply { scale(previewViewModel.zoomLevel.value) }
        svgImageProperty.set(svgImage)
    }
}