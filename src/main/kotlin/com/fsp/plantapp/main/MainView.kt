package com.fsp.plantapp

import com.fsp.plantapp.diagram.Diagram
import io.github.francois389.javaspringfx.annotations.View
import io.github.francois389.javaspringfx.navigation.IView
import io.github.francois389.javaspringfx.navigation.Navigator
import javafx.collections.ListChangeListener
import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import kotlin.uuid.Uuid

@View
class MainView(
    private val configuration: Configuration,
    navigator: Navigator,
    private val viewModel: MainViewModel
) : IView {
    private val tabPlus = Tab("+", navigator.findView(NouveauOngletView::class)).apply {
        isClosable = false
    }

    private val tabsByDiagram = HashMap<Uuid, Tab>()

    private val tabPane = TabPane()

    private fun addTab(diagram: Diagram, insertIndex: Int) {
        val tab = diagram.createTab(insertIndex).apply {
            isClosable = true
        }
        tabsByDiagram[diagram.id] = tab
        tabPane.tabs.add(insertIndex, tab)
    }

    override fun createUI(): Parent = tabPane.apply {
        viewModel.diagrams.forEachIndexed { index, diagram ->
            addTab(diagram, index)
        }
        tabPane.tabs.add(tabPlus)

        // Écoute des changements de la liste
        viewModel.diagrams.addListener(ListChangeListener { change ->
            while (change.next()) {
                if (change.wasAdded()) {
                    change.addedSubList.forEach { diagram ->
                        addTab(diagram, tabPane.tabs.size - 1) // insère avant le "+"
                    }
                }
                if (change.wasRemoved()) {
                    change.removed.forEach { diagram ->
                        tabsByDiagram.remove(diagram.id)?.let { tabPane.tabs.remove(it) }
                    }
                }
            }
            selectionModel.select((tabPane.tabs.size - 2).coerceIn(0, tabPane.tabs.size - 1))
        })
        selectionModel.select(0)
    }

    fun Diagram.createTab(index: Int = 0): Tab {
        return Tab(this.title, DiagramView(configuration, this).createUI()).apply {
            isClosable = true
            this@createTab.addTitreListener { newTitle ->
                newTitle.ifBlank { "?" }.let {
                    this.text = it
                }
            }
        }
    }
}

