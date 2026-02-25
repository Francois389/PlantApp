package com.fsp.plantapp

import javafx.scene.control.Button

fun Button(text: String, onClick: () -> Unit) = Button(text).apply {
    setOnAction { onClick() }
}