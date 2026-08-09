package com.fsp.plantapp.ouvrir

import com.fsp.plantapp.Configuration
import com.fsp.plantapp.util.Button
import com.fsp.plantapp.util.Insets
import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.FileChooser

@View
class OuvrirView(
    private val viewModel: OuvrirViewModel,
    private val configuration: Configuration
) : IView {
    override fun createUI(): Parent = VBox().apply {
        spacing = 10.0
        padding = Insets(100.0)
        alignment = Pos.TOP_CENTER
        children.addAll(
            Text("Ouvrir un diagramme à partir de son fichier"),
            findDiagramm
        )
    }

    val directorieChooser = FileChooser().apply {
        title = "Sélectionnez un diagramme à ouvrir"
        extensionFilters.addAll(
            FileChooser.ExtensionFilter("PlantApp diagramme", "*.${configuration.extension}"),
            FileChooser.ExtensionFilter("Image", "*.png")
        )
    }

    val findDiagramm = HBox().apply {
        spacing = 5.0
        alignment = Pos.CENTER_LEFT
        children.addAll(
            Label("Chemin vers le diagramme"),
            TextField().apply {
                textProperty().bindBidirectional(viewModel.diagrammPath)
                promptText = "/home/john/diagram"
                HBox.setHgrow(this, Priority.ALWAYS)
            },
            Button("Parcourir...") {


                directorieChooser.showOpenDialog(this.scene.window)?.let {
                    viewModel.diagrammPath.value = it.absolutePath
                }
            }
        )
    }
}