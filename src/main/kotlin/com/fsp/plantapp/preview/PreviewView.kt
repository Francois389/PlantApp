package com.fsp.plantapp.preview

import com.fsp.plantapp.Spacer
import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.girod.javafx.svgimage.SVGImage
import org.girod.javafx.svgimage.SVGLoader

@View
class PreviewView(
    private val previewViewModel: PreviewViewModel,
) : IView {

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
            }, previewParent
        )

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

    val svgImageProperty = SimpleObjectProperty<SVGImage?>(null)

    val previewParent = ScrollPane().apply {
        isPannable = true
        isFitToHeight = true
        isFitToWidth = true
        isCenterShape = true
        contentProperty().bind(svgImageProperty)
    }

    private fun updateDiagram(newValue: String?) {
        println("updateDiagram")
        val svgImage = SVGLoader.load(newValue)
            .apply { scale(previewViewModel.zoomLevel.value) }
        svgImageProperty.set(svgImage)
    }
}

class ZoomControl(
    reset: () -> Unit,
    plus: () -> Unit,
    moins: () -> Unit,
    label: StringBinding
) : HBox() {

    init {
        alignment = Pos.CENTER
        spacing = 5.0
        children.addAll(
            ZoomButton("-", moins),
            Label().apply {
                // Pourcentage 100%
                textProperty().bind(label)
            },
            ZoomButton("+", plus),
            Spacer(horizontalSize = 10.0),
            Button("Reset").apply {
                setOnAction {
                    reset()
                }
            }
        )
    }
}

class ZoomButton(string: String, action: () -> Unit) : Button(string) {
    init {
        setOnAction { action() }
    }
}
