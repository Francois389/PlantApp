package com.fsp.plantapp.diagram

import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset



class PlantUMLDiagram(
    source: String,
) : Cloneable {
    var title: String
    var image: ByteArray
    var source: String = source
        set(value) {
            field = value
            title = updateTitle(value)
            image = renderPNG(value)
        }

    init {
        this.title = updateTitle(source)
        this.image = renderPNG(source)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlantUMLDiagram

        if (this.source != other.source) return false
        if (title != other.title) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = this.source.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + image.contentHashCode()
        return result
    }

    private fun renderPNG(textSource: String): ByteArray {
        val reader = SourceStringReader(textSource)

        val pngOS = ByteArrayOutputStream()
        reader.outputImage(pngOS, FileFormatOption(FileFormat.PNG))

        val inputStream = pngOS.toByteArray().inputStream()

        return inputStream.readAllBytes()
    }

    fun renderSVG(): String {
        val reader = SourceStringReader(source)
        val os = ByteArrayOutputStream()

        reader.outputImage(os, FileFormatOption(FileFormat.SVG))
        os.close()


        return String(os.toByteArray(), Charset.forName("UTF-8"))
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