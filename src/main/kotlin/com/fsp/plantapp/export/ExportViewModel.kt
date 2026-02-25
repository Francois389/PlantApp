package com.fsp.plantapp.export

import com.fsp.plantapp.diagram.DiagramService
import com.fsp.plantapp.diagram.PlantUMLDiagram
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class ExportViewModel(
    private val exportService: DiagramService
) {
    val fileName = SimpleStringProperty()
    val directoryDestination = SimpleStringProperty()

    val diagram: Property<PlantUMLDiagram> = SimpleObjectProperty()

}
