package com.fsp.plantapp

import net.sourceforge.plantuml.SourceStringReader
import org.w3c.dom.Element
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOMetadataNode
import javax.xml.parsers.DocumentBuilderFactory

fun renderPlantUml(source: String): ByteArray {
    val out = ByteArrayOutputStream()
    SourceStringReader(source).outputImage(out)
    return out.toByteArray()
}

fun getTitleFromSource(source: String): String {
    val titleRegex = """title\s+(.*)""".toRegex(RegexOption.IGNORE_CASE)
    return titleRegex.find(source)?.groups?.get(1)?.value?.trim() ?: "diagram"
}

fun saveDiagramToFile(path: String, source : String, errorText: (String) -> Unit, successText: (String) -> Unit) {
    try {
        // Lire le PNG depuis les bytes
        val bufferedImage = renderPlantUml(source).inputStream().use { ImageIO.read(it) }
        // Préparer le writer PNG
        val writer = ImageIO.getImageWritersByFormatName("png").next()
        val writeParam = writer.defaultWriteParam
        val typeSpecifier = ImageTypeSpecifier.createFromRenderedImage(bufferedImage)
        val metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam)

        // Injecter le source PlantUML dans un chunk tEXt
        val metaFormat = "javax_imageio_png_1.0"
        val root = metadata.getAsTree(metaFormat) as Element
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val textNode = IIOMetadataNode("tEXt")
        val textEntry = IIOMetadataNode("tEXtEntry")
        textEntry.setAttribute("keyword", "plantuml_source")
        textEntry.setAttribute("value", source)
        textNode.appendChild(textEntry)
        root.appendChild(textNode)
        metadata.setFromTree(metaFormat, root)

        // Écrire le fichier
        val outputFile = File(path)
        val imageOutputStream = ImageIO.createImageOutputStream(outputFile)
        writer.output = imageOutputStream
        writer.write(metadata, IIOImage(bufferedImage, null, metadata), writeParam)
        imageOutputStream.close()
        writer.dispose()

        errorText("")
        successText("Diagramme exporté avec succès à : $path")
    } catch (e: Exception) {
        errorText("Erreur lors de la sauvegarde : ${e.message}")
        println(e.message)
    }
}