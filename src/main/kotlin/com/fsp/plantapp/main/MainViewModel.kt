package com.fsp.plantapp.main

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream
import java.io.InputStream

class MainViewModel {

    val textSource = SimpleStringProperty(
        """
        @startuml
        Alice -> Bob: Hello
        Bob -> Alice: Hi!
        @enduml
    """.trimIndent()
    )
    val imageOutput: Property<InputStream> = SimpleObjectProperty()
    val errorText = SimpleStringProperty()

    init {
        textSource.addListener { _, _, _ ->
            renderDiagram()
        }
        renderDiagram()
    }

    fun renderDiagram() {
        try {
            val source = textSource.value
            val reader = SourceStringReader(source)

            val outputStream = ByteArrayOutputStream()
            reader.outputImage(outputStream)

            val inputStream = outputStream.toByteArray().inputStream()
            imageOutput.value = inputStream
            errorText.value = ""
        } catch (e: Exception) {
            errorText.value = e.message
        }
    }
}