package com.fsp.plantapp.diagram

import org.springframework.stereotype.Service
import kotlin.uuid.Uuid

@Service
class DiagramService {
    private val diagrams: HashMap<Uuid, Diagram> = HashMap()
    private val creationDiagramsListener: MutableSet<(Diagram) -> Unit> = HashSet()

    fun getAll(): List<Diagram> {
        return diagrams.values.toList()
    }

    fun save(diagram: Diagram) {
        diagrams[diagram.id] = diagram
        creationDiagramsListener.forEach { it(diagram) }
    }

    fun find(id: Uuid): Diagram? {
        return diagrams[id]
    }

    fun onNouveauDiagramms(action: (Diagram) -> Unit) {
        creationDiagramsListener.add(action)
    }
}