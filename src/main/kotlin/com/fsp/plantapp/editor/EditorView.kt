package com.fsp.plantapp.editor

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.TextArea
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.web.WebView
import kotlin.math.max

class EditorView(viewModel: EditorViewModel) : SplitPane() {
    private var lastDragX = 0.0
    private var lastDragY = 0.0

    init {
        // --- PANNEAU GAUCHE (ÉDITEUR) ---
        val editor = VBox().apply {
            val textArea = TextArea().apply {
                prefRowCount = 10
                text = viewModel.source
                textProperty().subscribe(viewModel::handleSourceUpdate)
                font = Font.font("Monospaced")
            }
            VBox.setVgrow(textArea, Priority.ALWAYS)
            alignment = Pos.TOP_CENTER
            children.addAll(
                Label().apply { textProperty().bind(viewModel.title) },
                textArea,
            )
        }
        // --- PANNEAU DROIT (RENDU SVG) ---
        val webView = WebView()
        val scrollPane = ScrollPane(webView).apply {
            isPannable = true
            isFitToWidth = false
            isFitToHeight = false
        }

        enableDragPan(scrollPane, webView)

        // Abonnement aux changements du SVG
        viewModel.svgContent.subscribe { newSvg ->
            if (newSvg != null) {
                renderSvgInWebView(webView, newSvg)
            }
        }

        items.addAll(editor, scrollPane)
    }

    private fun enableDragPan(scrollPane: ScrollPane, webView: WebView) {
        webView.addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
            if (event.isPrimaryButtonDown) {
                lastDragX = event.sceneX
                lastDragY = event.sceneY
            }
        }

        webView.addEventFilter(MouseEvent.MOUSE_DRAGGED) { event ->
            if (!event.isPrimaryButtonDown) return@addEventFilter

            val deltaX = event.sceneX - lastDragX
            val deltaY = event.sceneY - lastDragY
            lastDragX = event.sceneX
            lastDragY = event.sceneY

            val viewport = scrollPane.viewportBounds
            val contentWidth = webView.boundsInLocal.width
            val contentHeight = webView.boundsInLocal.height
            val maxX = max(contentWidth - viewport.width, 0.0)
            val maxY = max(contentHeight - viewport.height, 0.0)

            if (maxX > 0.0) {
                val range = scrollPane.hmax - scrollPane.hmin
                val hStep = deltaX / maxX * range
                scrollPane.hvalue = (scrollPane.hvalue - hStep).coerceIn(scrollPane.hmin, scrollPane.hmax)
            }
            if (maxY > 0.0) {
                val range = scrollPane.vmax - scrollPane.vmin
                val vStep = deltaY / maxY * range
                scrollPane.vvalue = (scrollPane.vvalue - vStep).coerceIn(scrollPane.vmin, scrollPane.vmax)
            }

            event.consume()
        }
    }

    private fun renderSvgInWebView(webView: WebView, svgContent: String) {
        val engine = webView.engine
        val html = """
            <!DOCTYPE html><html><head><style>* { margin: 0; padding: 0; }body { display: flex; justify-content: center; background: white; }svg { width: 100%; height: auto; }</style></head>
            <body>$svgContent</body></html>
        """.trimIndent()

        engine.loadContent(html)

        // Ajustement dynamique de la taille de la WebView selon le contenu du SVG
        engine.documentProperty().addListener { _, _, doc ->
            if (doc != null) {
                val width =
                    engine.executeScript("document.querySelector('svg').getBBox().width").toString().toDoubleOrNull()
                        ?: 800.0
                val height =
                    engine.executeScript("document.querySelector('svg').getBBox().height").toString().toDoubleOrNull()
                        ?: 600.0

                webView.prefWidth = width + 20
                webView.prefHeight = height + 20
            }
        }
    }
}