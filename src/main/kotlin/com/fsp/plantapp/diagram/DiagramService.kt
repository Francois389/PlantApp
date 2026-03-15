package com.fsp.plantapp.diagram

import com.fsp.plantapp.Observable

class DiagramService(
    private val diagramRepository: InMemoryDiagramRepository,
    private val listeners: MutableList<(() -> Unit)> = mutableListOf(),
): Observable {
    fun getDiagram(): PlantUMLDiagram {
        return diagramRepository.get().clone()
    }

    override fun update() = listeners.forEach { it() }

    override fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun updateDiagram(newDiagram: PlantUMLDiagram) {
        diagramRepository.save(newDiagram)
        update()
    }

}