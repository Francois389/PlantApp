package com.fsp.plantapp.diagram

data class PlantUMLDiagram(
    val diagrammSource: String,
    val diagrammTitle: String,
    val diagrammImage: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlantUMLDiagram

        if (diagrammSource != other.diagrammSource) return false
        if (diagrammTitle != other.diagrammTitle) return false
        if (!diagrammImage.contentEquals(other.diagrammImage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = diagrammSource.hashCode()
        result = 31 * result + diagrammTitle.hashCode()
        result = 31 * result + diagrammImage.contentHashCode()
        return result
    }
}