package com.fsp.plantapp.util

import javafx.geometry.Insets
import javafx.scene.control.Button

fun Number.estPositif(): Boolean = this.toDouble() > 0.0

fun Button(text: String, onClick: () -> Unit) = Button(text).apply {
    setOnAction { onClick() }
}

class Insets(horizontal: Double = 0.0, vertical: Double = 0.0) : Insets(vertical, horizontal, vertical, horizontal)