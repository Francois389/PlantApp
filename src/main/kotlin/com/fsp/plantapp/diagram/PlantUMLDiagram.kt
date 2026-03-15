package com.fsp.plantapp.diagram

import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream

class PlantUMLDiagram(
    source: String,
): Cloneable {
    var title: String
    var image: ByteArray
    var source: String = source
        set(value) {
            field = value
            title = updateTitle(value)
            image = renderDiagram(value)
        }

    init {
        this.title = updateTitle(source)
        this.image = renderDiagram(source)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlantUMLDiagram

        if (this@PlantUMLDiagram.source != other.source) return false
        if (title != other.title) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = this@PlantUMLDiagram.source.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }

    private fun renderDiagram(textSource: String): ByteArray {
        val reader = SourceStringReader(textSource)

        val outputStream = ByteArrayOutputStream()
        reader.outputImage(outputStream)

        val inputStream = outputStream.toByteArray().inputStream()

        return inputStream.readAllBytes()
    }

    private fun updateTitle(textSource: String): String {
        return textSource
            .split("\n")
            .firstOrNull { it.contains("title") }
            ?.substringAfter("title")
            ?.trim() ?: ""
    }

    public override fun clone(): PlantUMLDiagram {
        return PlantUMLDiagram(source)
    }
}