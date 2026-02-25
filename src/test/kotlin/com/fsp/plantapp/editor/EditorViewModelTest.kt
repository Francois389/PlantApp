package com.fsp.plantapp.editor

import com.fsp.plantapp.diagram.DiagramService
import com.fsp.plantapp.diagram.InMemoryDiagramRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EditorViewModelTest {

    @Test
    fun `title update correctly`() {
        // Given a view model with a simple diagram
        val viewModel = EditorViewModel(DiagramService(InMemoryDiagramRepository()))
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