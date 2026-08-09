package com.fsp.plantapp

import com.fsp.plantapp.ouvrir.OuvrirView
import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import io.github.francois389.javaspringfx.navigation.Navigator
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport

@View
class NouveauOngletView(
    private val navigator: Navigator,
    private val viewModel: NouveauOngletViewModel,
    private val placeholderConfigurerSupport: PlaceholderConfigurerSupport
) : IView {
    val ouvrir = StackPane().apply {
        Button("Ouvrir").apply {
            onAction = { viewModel.onOuvrirClick() }
        }
            .let(this.children::add)
    }

    val nouveau = StackPane().apply {
        Button("Nouveau").apply {
            onAction = { viewModel.onNouveauClick() }
        }
            .let(this.children::add)
    }

    val liste = StackPane().apply {
        Text("TODO") // TODO
            .let(this.children::add)
    }

    val actionSelection = SplitPane().apply {
        val gauche = StackPane().apply {
            SplitPane().apply {
                orientation = Orientation.VERTICAL
                items.addAll(nouveau, ouvrir)
            }.let(this.children::add)
        }
        items.addAll(gauche, liste)
        VBox.setVgrow(this, Priority.ALWAYS)
    }

    val parent = VBox().apply {
        children.add(actionSelection)
    }

    init {
        viewModel.afficherAction.addListener { _, _, new ->
            if (new == true) {
                parent.children.replaceAll({ actionSelection })
            } else {
                parent.children.replaceAll({ navigator.findView(OuvrirView::class) })
            }
        }
    }

    override fun createUI(): Parent = parent
}