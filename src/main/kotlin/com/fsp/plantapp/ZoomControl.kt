package com.fsp.plantapp

import javafx.beans.binding.StringBinding
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox

class ZoomControl(
    reset: () -> Unit,
    plus: () -> Unit,
    moins: () -> Unit,
    label: StringBinding
) : HBox() {

    init {
        alignment = Pos.CENTER
        spacing = 5.0
        children.addAll(
            ZoomButton("-", moins),
            Label().apply {
                textProperty().bind(label)
            },
            ZoomButton("+", plus),
            Spacer(horizontalSize = 10.0),
            Button("Reset").apply {
                setOnAction {
                    reset()
                }
            }
        )
    }
}

class ZoomButton(string: String, action: () -> Unit) : Button(string) {
    init {
        setOnAction { action() }
    }
}