package com.fsp.plantapp

import javafx.scene.shape.Box


class Spacer(
    verticalSize: Double = 0.0,
    horizontalSize: Double = 0.0,
) : Box() {

    init {
        if (verticalSize > 0.0) {
            height = verticalSize
        }
        if (horizontalSize > 0.0) {
            width = horizontalSize
        }
        isVisible = false
    }
}