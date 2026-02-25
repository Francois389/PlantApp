package com.fsp.plantapp.editor

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EditorViewModelTest {

    @Test
    fun `title update correctly`() {
        // Given a view model with a simple diagram
        val viewModel = EditorViewModel()
        val title = "Titre"
        val simpleDiagram = """
            @startuml;
            title $title
            @enduml
        """.trimIndent()

        //When we update the diagram
        viewModel.textSource.value = simpleDiagram

        // Then the title should be updated
        assertEquals(title, viewModel.diagramTitle.value)
    }
}