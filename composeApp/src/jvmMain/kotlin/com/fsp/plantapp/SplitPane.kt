package com.fsp.plantapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import java.awt.Cursor

enum class Orientation {
    Vertical, Horizontal
}

@Composable
fun SplitPane(
    firstPanel: @Composable () -> Unit,
    secondPanel: @Composable () -> Unit,
    orientation: Orientation = Orientation.Vertical,
) {
    when (orientation) {
        Orientation.Vertical -> VerticalSplitPanel(firstPanel, secondPanel)
        Orientation.Horizontal -> HorizontalSplitPanel(firstPanel, secondPanel)
    }
}


/**
 * [Original
 * Author](https://github.com/mlackman/kotlin-multiplaform-splitpane/blob/main/src/main/kotlin/main.kt)
 *
 * @author mlackman
 */
@Composable
fun VerticalSplitPanel(
    leftPanel: @Composable () -> Unit,
    rightPanel: @Composable () -> Unit,
) {
    var leftPaneWidth by remember { mutableStateOf(150.dp) }
    Row {
        Column(modifier = Modifier.width(leftPaneWidth).fillMaxHeight()) {
            leftPanel()
        }
        Column(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .background(Color(234, 234, 234))
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        leftPaneWidth += dragAmount.x.toDp();
                    }
                }
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)))
                .border(1.dp, Color.Gray)
        ) {}
        Column(modifier = Modifier.fillMaxSize()) {
            rightPanel()
        }
    }
}


/**
 * [Original
 * Author](https://github.com/mlackman/kotlin-multiplaform-splitpane/blob/main/src/main/kotlin/main.kt)
 *
 * @author mlackman
 */
@Composable
fun HorizontalSplitPanel(
    topPanel: @Composable () -> Unit,
    bottomPanel: @Composable () -> Unit,
) {
    var topPanelHeight by remember { mutableStateOf(150.dp) }
    Column {
        // Top panel
        Row(modifier = Modifier.height(topPanelHeight).fillMaxWidth()) {
            topPanel()
        }

        // Bottom panel
        Row(
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth()
                .background(Color(234, 234, 234))
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        topPanelHeight += dragAmount.y.toDp();
                    }
                }
                .pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)))
                .border(1.dp, Color.Gray)
        ) {}
        Row(modifier = Modifier.fillMaxSize()) {
            bottomPanel()
        }
    }
}