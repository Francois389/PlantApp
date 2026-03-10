package com.fsp.plantapp.diagram

import com.fsp.plantapp.Observer
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream

class DiagramService(
    private val diagramRepository: InMemoryDiagramRepository,
    private val listeners: MutableList<(() -> Unit)> = mutableListOf(),
): Observer {
    init {
        regenerateDiagramImage()
    }

    fun regenerateDiagramImage() {
        val diagram = diagramRepository.get()
        if (diagram.diagrammImage.isEmpty()) {
            setDiagramSource(diagram.diagrammSource)
        }
    }

    private fun renderDiagram(textSource: String): ByteArray {
        val reader = SourceStringReader(textSource)

        val outputStream = ByteArrayOutputStream()
        reader.outputImage(outputStream)

        val inputStream = outputStream.toByteArray().inputStream()

        return inputStream.readAllBytes()
    }

    fun setDiagramSource(diagramSource: String) {
        val diagramTitle = updateTitle(diagramSource)
        val diagramImage = renderDiagram(diagramSource)

        val diagram = PlantUMLDiagram(
            diagrammSource = diagramSource,
            diagrammTitle = diagramTitle,
            diagrammImage = diagramImage
        )

        diagramRepository.save(diagram)
        update()
    }

    private fun updateTitle(textSource: String): String {
        return textSource
            .split("\n")
            .firstOrNull { it.contains("title") }
            ?.substringAfter("title")
            ?.trim() ?: ""
    }

    fun getDiagram(): PlantUMLDiagram {
        return diagramRepository.get().copy()
    }

    override fun update() = listeners.forEach { it() }

    override fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }
}