package com.fsp.plantapp.diagram

class InMemoryDiagramRepository {
    private var diagram: PlantUMLDiagram

    init {
        diagram = PlantUMLDiagram(
            source = """
            @startuml;
            title Titre
            Alice -> Bob: Hello
            Bob -> Alice: Hi!
            @enduml
            """.trimIndent(),
        )
    }

    fun save(diagram: PlantUMLDiagram) {
        this.diagram = diagram
    }

    fun get(): PlantUMLDiagram {
        return diagram
    }
}