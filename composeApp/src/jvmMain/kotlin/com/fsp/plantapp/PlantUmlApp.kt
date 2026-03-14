package com.fsp.plantapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import net.sourceforge.plantuml.SourceStringReader
import java.io.ByteArrayOutputStream
import org.jetbrains.skia.Image as ImageSkia

@Composable
fun PlantApp() {
    var source by remember {
        mutableStateOf(
            """
            @startuml
            Alice -> Bob: Hello
            Bob --> Alice: Hi!
            @enduml
            """.trimIndent()
        )
    }
    var diagram by remember { mutableStateOf<ImageBitmap?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // Regénère le diagramme à chaque modification du texte
    LaunchedEffect(source) {
        try {
            val png = renderPlantUml(source)
            diagram = ImageSkia.makeFromEncoded(png).toComposeImageBitmap()
            error = null
        } catch (e: Exception) {
            error = e.message
        }
    }

    MaterialTheme {
        SplitPane(
            firstPanel = {
                EditorPanel(source) {
                    source = it
                }
            },
            secondPanel = { PreviewPanel(error, diagram) },
            orientation = Orientation.Vertical
        )
    }
}

@Composable
fun PreviewPanel(
    error: String?,
    diagram: ImageBitmap?
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        when {
            error != null -> Text(
                text = "Erreur : $error",
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(12.dp)
            )

            diagram != null -> Image(
                bitmap = diagram,
                contentDescription = "Diagramme PlantUML",
                modifier = Modifier.fillMaxSize()
            )

            else -> CircularProgressIndicator()
        }
    }
}

@Composable
fun EditorPanel(source: String, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = source,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxHeight()
            .background(MaterialTheme.colors.surface)
            .padding(12.dp)
    )
}

// Génération PNG via le jar PlantUML embarqué (100% local, pas de réseau)
fun renderPlantUml(source: String): ByteArray {
    val out = ByteArrayOutputStream()
    SourceStringReader(source).outputImage(out)
    return out.toByteArray()
}