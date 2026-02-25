package com.fsp.plantapp.diagram

class InMemoryDiagramRepository {
    private var diagram: PlantUMLDiagram

    init {
        diagram = PlantUMLDiagram(
            diagrammSource = """
                            @startuml;
                            title Titre
                            Alice -> Bob: Hello
                            Bob -> Alice: Hi!
                            @enduml
                            """.trimIndent(),
            diagrammTitle = "Titre",
            diagrammImage = ByteArray(0)
        )
    }

    fun save(diagram: PlantUMLDiagram) {
        this.diagram = diagram
    }

    fun get(): PlantUMLDiagram {
        return diagram
    }
}