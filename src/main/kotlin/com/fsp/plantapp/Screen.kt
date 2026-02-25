package com.fsp.plantapp

sealed class Screen(val width: Double = 600.0, val heigth: Double = 500.0) {
    object EditorScreen : Screen(1000.0, 600.0)
    object ExportScreen : Screen()
}