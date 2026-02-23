package com.fsp.plantapp.main

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MainViewModelTest {

    @Test
    fun `title update correctly`() {
        // Given a view model with a simple diagram
        val viewModel = MainViewModel()
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