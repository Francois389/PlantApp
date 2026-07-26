package com.fsp.plantapp.util

import javafx.geometry.Insets
import javafx.scene.control.Button
import java.text.Normalizer
import java.util.*
import java.util.Locale.getDefault

fun Number.estPositif(): Boolean = this.toDouble() > 0.0

fun Button(text: String, onClick: () -> Unit) = Button(text).apply {
    setOnAction { onClick() }
}

class Insets(horizontal: Double = 0.0, vertical: Double = 0.0) : Insets(vertical, horizontal, vertical, horizontal)

fun String.capitalize(local: Locale = getDefault()): String = this.replaceFirstChar {
    if (it.isLowerCase()) {
        it.titlecase(local)
    } else {
        it.toString()
    }
}

fun String.removeAccent(): String = Normalizer.normalize(this, Normalizer.Form.NFD)
    .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")