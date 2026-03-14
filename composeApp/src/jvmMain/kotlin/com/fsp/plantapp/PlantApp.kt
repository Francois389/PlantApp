package com.fsp.plantapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PlantApp() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("PlantUmlApp", "Export")

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

    MaterialTheme {
        Column {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier.background(Color.Gray)
                    )
                }
            }
            when (selectedTab) {
                0 -> PlantUmlEditor(source) {
                    source = it
                }
                1 -> ExportForm(source)
            }
        }
    }
}

