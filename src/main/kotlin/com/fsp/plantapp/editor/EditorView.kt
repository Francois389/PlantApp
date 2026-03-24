package com.fsp.plantapp.editor

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import kotlin.math.abs
import kotlin.math.max

class EditorView(viewModel: EditorViewModel) : SplitPane() {
    private var lastDragX = 0.0
    private var lastDragY = 0.0
    private var zoomFactor = 1.0
    private val minZoom = 0.25
    private val maxZoom = 4.0
    private val zoomStep = 1.1
    private var svgBaseWidth = 800.0
    private var svgBaseHeight = 600.0

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
        val zoomLabel = Label(formatZoomLabel())
        val zoomBar = HBox(8.0).apply {
            alignment = Pos.CENTER_LEFT
            children.addAll(
                Button("-").apply {
                    setOnAction { applyZoom(scrollPane, webView, zoomFactor / zoomStep, zoomLabel) }
                },
                Button("+").apply {
                    setOnAction { applyZoom(scrollPane, webView, zoomFactor * zoomStep, zoomLabel) }
                },
                Button("100%").apply {
                    setOnAction { applyZoom(scrollPane, webView, 1.0, zoomLabel) }
                },
                zoomLabel,
            )
        }
        val preview = VBox(8.0, zoomBar, scrollPane).apply {
            alignment = Pos.TOP_LEFT
            VBox.setVgrow(scrollPane, Priority.ALWAYS)
        }

        enableDragPan(scrollPane, webView)
        enableWheelZoom(scrollPane, webView, zoomLabel)

        // Abonnement aux changements du SVG
        viewModel.svgContent.subscribe { newSvg ->
            if (newSvg != null) {
                renderSvgInWebView(scrollPane, webView, newSvg, zoomLabel)
            }
        }

        items.addAll(editor, preview)
    }

    private fun enableDragPan(scrollPane: ScrollPane, webView: WebView) {
        webView.addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
            if (event.isPrimaryButtonDown) {
                lastDragX = event.sceneX
                lastDragY = event.sceneY
            }
        }

        webView.addEventFilter(MouseEvent.MOUSE_DRAGGED) { event ->
            if (event.isPrimaryButtonDown) {
                val deltaX = event.sceneX - lastDragX
                val deltaY = event.sceneY - lastDragY
                lastDragX = event.sceneX
                lastDragY = event.sceneY

                val viewport = scrollPane.viewportBounds
                val contentWidth = webView.boundsInParent.width
                val contentHeight = webView.boundsInParent.height
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
    }

    private fun enableWheelZoom(scrollPane: ScrollPane, webView: WebView, zoomLabel: Label) {
        webView.addEventFilter(ScrollEvent.SCROLL) { event ->
            if (event.isControlDown && event.deltaY != 0.0) {
                val factor = if (event.deltaY > 0) zoomStep else 1 / zoomStep
                applyZoom(scrollPane, webView, zoomFactor * factor, zoomLabel)
                event.consume()
            }
        }
    }

    private fun applyZoom(
        scrollPane: ScrollPane,
        webView: WebView,
        requestedZoom: Double,
        zoomLabel: Label,
        forceApply: Boolean = false,
    ) {
        val clamped = requestedZoom.coerceIn(minZoom, maxZoom)

        if (!forceApply && abs(clamped - zoomFactor) < 0.0001) {
            zoomLabel.text = formatZoomLabel()
            return
        }

        val viewport = scrollPane.viewportBounds
        val oldContentWidth = webView.boundsInParent.width
        val oldContentHeight = webView.boundsInParent.height
        val oldMaxX = max(oldContentWidth - viewport.width, 0.0)
        val oldMaxY = max(oldContentHeight - viewport.height, 0.0)
        val hRange = scrollPane.hmax - scrollPane.hmin
        val vRange = scrollPane.vmax - scrollPane.vmin
        val oldX = if (hRange > 0.0) (scrollPane.hvalue - scrollPane.hmin) / hRange * oldMaxX else 0.0
        val oldY = if (vRange > 0.0) (scrollPane.vvalue - scrollPane.vmin) / vRange * oldMaxY else 0.0

        zoomFactor = clamped
        applyZoomToSvg(webView.engine)
        val zoomedWidth = svgBaseWidth * zoomFactor
        val zoomedHeight = svgBaseHeight * zoomFactor
        webView.prefWidth = zoomedWidth + 20
        webView.prefHeight = zoomedHeight + 20
        zoomLabel.text = formatZoomLabel()

        Platform.runLater {
            val newMaxX = max(webView.boundsInParent.width - scrollPane.viewportBounds.width, 0.0)
            val newMaxY = max(webView.boundsInParent.height - scrollPane.viewportBounds.height, 0.0)

            when {
                newMaxX > 0.0 && hRange > 0.0 -> {
                    val xRatio = if (oldMaxX > 0.0) (oldX / oldMaxX).coerceIn(0.0, 1.0) else 0.0
                    scrollPane.hvalue = scrollPane.hmin + xRatio * hRange
                }

                else -> scrollPane.hvalue = scrollPane.hmin
            }
            when {
                newMaxY > 0.0 && vRange > 0.0 -> {
                    val yRatio = if (oldMaxY > 0.0) (oldY / oldMaxY).coerceIn(0.0, 1.0) else 0.0
                    scrollPane.vvalue = scrollPane.vmin + yRatio * vRange
                }

                else -> scrollPane.vvalue = scrollPane.vmin
            }
        }
    }

    private fun applyZoomToSvg(engine: WebEngine) {
        try {
            engine.executeScript("if (window.__setSvgZoom) { window.__setSvgZoom($zoomFactor); }")
        } catch (_: Exception) {
            // Le SVG n'est pas encore prêt, un prochain rendu relancera l'application du zoom.
        }
    }

    private fun readScriptDouble(engine: WebEngine, script: String, fallback: Double): Double =
        try {
            engine.executeScript(script).toString().toDoubleOrNull() ?: fallback
        } catch (_: Exception) {
            fallback
        }

    private fun formatZoomLabel(): String = "${(zoomFactor * 100).toInt()}%"

    private fun renderSvgInWebView(scrollPane: ScrollPane, webView: WebView, svgContent: String, zoomLabel: Label) {
        val engine = webView.engine
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { background: white; overflow: hidden; }
                    #svg-container { display: block; position: relative; }
                    svg { display: block; transform-origin: top left; }
                </style>
            </head>
            <body>
                <div id="svg-container">$svgContent</div>
                <script>
                    (function () {
                        const container = document.getElementById('svg-container');
                        const svg = container ? container.querySelector('svg') : null;
                        let baseWidth = 800;
                        let baseHeight = 600;

                        if (svg) {
                            try {
                                const box = svg.getBBox();
                                if (box && box.width > 0 && box.height > 0) {
                                    baseWidth = box.width;
                                    baseHeight = box.height;
                                }
                            } catch (e) {
                                // Fallback silencieux si getBBox échoue.
                            }
                            const vb = svg.viewBox && svg.viewBox.baseVal;
                            if ((baseWidth <= 0 || baseHeight <= 0) && vb && vb.width > 0 && vb.height > 0) {
                                baseWidth = vb.width;
                                baseHeight = vb.height;
                            }

                            svg.style.width = baseWidth + 'px';
                            svg.style.height = baseHeight + 'px';
                            svg.style.transformOrigin = 'top left';
                        }

                        window.__svgBaseWidth = baseWidth;
                        window.__svgBaseHeight = baseHeight;

                        window.__setSvgZoom = function (zoom) {
                            if (!svg || !container) {
                                return false;
                            }
                            svg.style.transform = 'scale(' + zoom + ')';
                            container.style.width = (window.__svgBaseWidth * zoom) + 'px';
                            container.style.height = (window.__svgBaseHeight * zoom) + 'px';
                            return true;
                        };

                        window.__getSvgZoomedWidth = function () {
                            return container ? Math.max(container.scrollWidth, container.clientWidth) : window.__svgBaseWidth;
                        };
                        window.__getSvgZoomedHeight = function () {
                            return container ? Math.max(container.scrollHeight, container.clientHeight) : window.__svgBaseHeight;
                        };
                    })();
                </script>
            </body>
            </html>
        """.trimIndent()

        engine.loadContent(html)

        // Ajustement dynamique de la taille de la WebView selon le contenu du SVG
        engine.documentProperty().addListener { _, _, doc ->
            if (doc != null) {
                val width = readScriptDouble(engine, "window.__svgBaseWidth || 800", 800.0)
                val height = readScriptDouble(engine, "window.__svgBaseHeight || 600", 600.0)

                svgBaseWidth = width
                svgBaseHeight = height
                applyZoom(scrollPane, webView, zoomFactor, zoomLabel, forceApply = true)
            }
        }
    }
}