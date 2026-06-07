package com.fsp.plantapp.diagram

import net.sourceforge.plantuml.SourceStringReader
import org.springframework.stereotype.Service
import java.io.OutputStream

@Service
class DiagramService() {
        fun renderDiagram(source: String, output: OutputStream): Boolean {
            val reader = SourceStringReader(source)
            val isSuccess = reader.outputImage(output).description != null
            return isSuccess
        }
}