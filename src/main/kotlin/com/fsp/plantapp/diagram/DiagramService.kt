package com.fsp.plantapp.diagram

import com.fsp.plantapp.observable.Observable
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset


@Service
class DiagramService : Observable<String>() {

    var diagramSource: String = """
        @startuml;
        title Titre
        Alice -> Bob: Hello
        Bob -> Alice: Hi!
        @enduml
    """.trimIndent()
        set(value) {
            field = value
            updateTitle()
            notifyObservers(value)
        }

    final var title: String = extractTitle() ?: ""
        private set

    fun renderDiagram(output: OutputStream): Boolean {
        if (diagramSource.isBlank()) return false
        val reader = SourceStringReader(diagramSource)
        val isSuccess = reader.outputImage(output).description != null
        return isSuccess
    }

    fun getSVG(): String {
        val reader = SourceStringReader(diagramSource)
        val output = ByteArrayOutputStream()
        reader.outputImage(output, FileFormatOption(FileFormat.SVG))
        output.close()
        return String(output.toByteArray(), Charset.forName("UTF-8"))
    }

    private fun updateTitle() = extractTitle()
        ?.let { title = it }

    private fun extractTitle(): String? = diagramSource
        .split("\n")
        .firstOrNull { it.startsWith("title ") }
        ?.substringAfter("title ")
        ?.trim()
}