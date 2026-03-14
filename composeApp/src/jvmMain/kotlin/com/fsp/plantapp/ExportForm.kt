package com.fsp.plantapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
fun ExportForm(source: String) {
    var title by remember(source) { mutableStateOf(getTitleFromSource(source)) }
    var destinationFolder by remember { mutableStateOf(System.getProperty("user.home")) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Exporter le diagramme", style = MaterialTheme.typography.h4)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre du fichier (sans extension)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = destinationFolder,
                onValueChange = { destinationFolder = it },
                label = { Text("Dossier de destination") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val fileDialog = FileDialog(Frame(), "Choisir la destination", FileDialog.SAVE)
                fileDialog.directory = destinationFolder
                fileDialog.file = title
                fileDialog.isVisible = true

                if (fileDialog.file != null && fileDialog.directory != null) {
                    destinationFolder = fileDialog.directory
                    title = fileDialog.file
                }
            }) {
                Text("Parcourir ...")
            }
        }

        Button(
            onClick = {
                feedbackMessage = ""
                if (title.isBlank()) {
                    feedbackMessage = "Le titre ne peut pas être vide"
                    isError = true
                    return@Button
                }

                val folder = File(destinationFolder)
                if (!folder.exists()) {
                    feedbackMessage = "Le dossier de destination n'existe pas"
                    isError = true
                    return@Button
                }

                val fileName = if (title.endsWith(".png", ignoreCase = true)) title else "$title.png"
                val fullPath = File(folder, fileName).absolutePath

                saveDiagramToFile(
                    path = fullPath,
                    source = source,
                    errorText = { err ->
                        feedbackMessage = err
                        isError = true
                    },
                    successText = { msg ->
                        feedbackMessage = msg
                        isError = false
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Exporter")
        }

        if (feedbackMessage.isNotEmpty()) {
            Text(
                text = feedbackMessage,
                color = if (isError) MaterialTheme.colors.error else MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}