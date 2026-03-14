package com.fsp.plantapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
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
        delay(300) // debounce 300ms
        try {
            val png = renderPlantUml(source)
            diagram = ImageSkia.makeFromEncoded(png).toComposeImageBitmap()
            error = null
        } catch (e: Exception) {
            error = e.message
        }
    }

    MaterialTheme {
        Row(Modifier.fillMaxSize().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            // Panneau éditeur (gauche)
            BasicTextField(
                value = source,
                onValueChange = { source = it },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colors.surface)
                    .padding(12.dp)
            )

            // Panneau prévisualisation (droite)
            Box(Modifier.weight(1f).fillMaxHeight()) {
                when {
                    error != null -> Text(
                        text = "Erreur : $error",
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.padding(12.dp)
                    )
                    diagram != null -> Image(
                        bitmap = diagram!!,
                        contentDescription = "Diagramme PlantUML",
                        modifier = Modifier.fillMaxSize()
                    )
                    else -> CircularProgressIndicator()
                }
            }
        }
    }
}

// Génération PNG via le jar PlantUML embarqué (100% local, pas de réseau)
fun renderPlantUml(source: String): ByteArray {
    val out = ByteArrayOutputStream()
    SourceStringReader(source).generateImage(out)
    return out.toByteArray()
}