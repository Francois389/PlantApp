package com.fsp.plantapp

import com.fsp.plantapp.diagram.Diagram
import com.fsp.plantapp.diagram.DiagramService
import io.github.francois389.javaspringfx.annotations.ViewModel
import io.github.francois389.javaspringfx.navigation.Navigator
import javafx.beans.property.SimpleBooleanProperty

@ViewModel
class NouveauOngletViewModel(
    private val navigator: Navigator,
    private val diagramService: DiagramService
) {

    val afficherAction = SimpleBooleanProperty(true)


    fun onNouveauClick() {
        val nouveauDiagram = Diagram()
        diagramService.save(nouveauDiagram)
        // TODO navigate to new diagram created
    }

    fun onOuvrirClick() {
        afficherAction.value = false
    }
}
