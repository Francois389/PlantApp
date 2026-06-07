package com.fsp.plantapp.diagram

import net.sourceforge.plantuml.SourceStringReader
import org.springframework.stereotype.Service
import java.io.OutputStream

@Service
class DiagramService {

    var diagramSource: String = """
        @startuml;
        title Titre
        Alice -> Bob: Hello
        Bob -> Alice: Hi!
        @enduml
    """.trimIndent()

    fun renderDiagram(output: OutputStream): Boolean {
        val reader = SourceStringReader(diagramSource)
        val isSuccess = reader.outputImage(output).description != null
        return isSuccess
    }
}