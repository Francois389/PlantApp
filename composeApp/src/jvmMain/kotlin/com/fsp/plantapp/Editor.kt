package com.fsp.plantapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.decodeToImageBitmap

@Composable
fun PlantUmlEditor(source: String, onSourceChange: (String) -> Unit = {}) {
    var diagram by remember { mutableStateOf<ImageBitmap?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // Regénère le diagramme à chaque modification du texte
    LaunchedEffect(source) {
        try {
            val png = renderPlantUml(source)
            diagram = png.inputStream().readAllBytes().decodeToImageBitmap()
            error = null
        } catch (e: Exception) {
            error = e.message
        }
    }

    MaterialTheme {
        SplitPane(
            firstPanel = {
                EditorPanel(source, onSourceChange)
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
        contentAlignment = Alignment.Companion.Center,
        modifier = Modifier.Companion
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        when {
            error != null -> Text(
                text = "Erreur : $error",
                color = MaterialTheme.colors.error,
                modifier = Modifier.Companion.padding(12.dp)
            )

            diagram != null -> Image(
                bitmap = diagram,
                contentDescription = "Diagramme PlantUML",
                modifier = Modifier.Companion.fillMaxSize()
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
        modifier = Modifier.Companion
            .fillMaxHeight()
            .padding(12.dp)
    )
}

