package com.fsp.plantapp.diagram

import com.fsp.plantapp.observable.Observable
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class Diagram(
    initialDiagram: String = """
            @startuml;
            title Titre
            Alice -> Bob: Hello
            Bob -> Alice: Hi!
            @enduml
        """.trimIndent()
) : Observable<String>() {

    val titreListener: MutableList<(String) -> Unit> = mutableListOf()

    @OptIn(ExperimentalUuidApi::class)
    val id = Uuid.generateV7()

    var diagramSource: String = initialDiagram
        set(value) {
            field = value
            updateTitle()
            notifyObservers(value)
        }

    var title: String = extractTitle() ?: ""
        private set
        get() = extractTitle() ?: field

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
        ?.let { if (title == it) it else null }
        ?.let {
            title = it
            titreListener.forEach { listener -> listener(it) }
        }

    private fun extractTitle(): String? = diagramSource
        .split("\n")
        .firstOrNull { it.startsWith("title ") }
        ?.substringAfter("title ")
        ?.trim()

    fun addTitreListener(fonction: (String) -> Unit) {
        titreListener += fonction
    }

}