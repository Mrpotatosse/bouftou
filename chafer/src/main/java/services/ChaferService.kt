package services

import adapter.services.WorldAdapterService
import com.formdev.flatlaf.FlatDarkLaf
import d2o.entities.D2ODataType
import d2o.services.D2OService
import d2p.services.D2PService
import dlm.services.MapService
import ele.services.ElementsService
import entities.BlopEntry
import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import java.awt.*
import java.awt.event.*
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.nio.file.Paths
import javax.swing.*
import javax.swing.border.EmptyBorder

// ── Palette ──────────────────────────────────────────────────────────────────
private object Theme {
    val bg = Color(0x12, 0x14, 0x1A)
    val surface = Color(0x1C, 0x1F, 0x28)
    val surfaceHigh = Color(0x24, 0x28, 0x35)
    val border = Color(0x2E, 0x33, 0x44)
    val accent = Color(0x5B, 0x9B, 0xFF)
    val accentDim = Color(0x2A, 0x45, 0x7A)
    val fg = Color(0xE4, 0xE6, 0xF0)
    val fgMuted = Color(0x7A, 0x80, 0x99)
}

// ── Reusable UI primitives ────────────────────────────────────────────────────

/** A label styled to the dark theme. */
private fun label(text: String, size: Float = 12f, bold: Boolean = false, color: Color = Theme.fg): JLabel =
    JLabel(text).apply {
        foreground = color
        font = Font("JetBrains Mono", if (bold) Font.BOLD else Font.PLAIN, 0)
            .deriveFont(size)
        // Fallback if JetBrains Mono is absent
        if (font.family == "Dialog") font = Font(Font.MONOSPACED, if (bold) Font.BOLD else Font.PLAIN, size.toInt())
    }

/** Compass / nav button with a subtle glow on hover. */
private fun navButton(symbol: String): JButton = object : JButton(symbol) {
    private var hovered = false

    init {
        isContentAreaFilled = false
        isBorderPainted = false
        isFocusPainted = false
        foreground = Theme.fg
        font = Font(Font.SANS_SERIF, Font.BOLD, 18)
        cursor = Cursor(Cursor.HAND_CURSOR)
        preferredSize = Dimension(48, 48)
        addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                hovered = true; repaint()
            }

            override fun mouseExited(e: MouseEvent) {
                hovered = false; repaint()
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val shape = RoundRectangle2D.Float(1f, 1f, width - 2f, height - 2f, 10f, 10f)
        if (hovered) {
            g2.color = Theme.accentDim
            g2.fill(shape)
            g2.color = Theme.accent
            g2.stroke = BasicStroke(1.2f)
            g2.draw(shape)
            foreground = Theme.accent
        } else {
            g2.color = Theme.surfaceHigh
            g2.fill(shape)
            g2.color = Theme.border
            g2.stroke = BasicStroke(1f)
            g2.draw(shape)
            foreground = Theme.fgMuted
        }
        super.paintComponent(g)
    }
}.also { it.isEnabled = false /* enabled once map loads */ }

// ── Spinning loader overlay ───────────────────────────────────────────────────

private class LoadingOverlay : JComponent() {
    private var angle = 0f
    private val timer = Timer(16) { angle = (angle + 6f) % 360f; repaint() }

    init {
        isOpaque = false
    }

    fun start() {
        isVisible = true; timer.start()
    }

    fun stop() {
        timer.stop(); isVisible = false
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        // Dim overlay
        g2.color = Color(0, 0, 0, 160)
        g2.fillRect(0, 0, width, height)
        // Arc spinner
        val cx = width / 2
        val cy = height / 2
        val r = 28
        g2.color = Theme.border
        g2.stroke = BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g2.drawOval(cx - r, cy - r, r * 2, r * 2)
        g2.color = Theme.accent
        val old = g2.stroke
        g2.stroke = BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g2.drawArc(cx - r, cy - r, r * 2, r * 2, angle.toInt(), 90)
        g2.stroke = old
        // Label
        g2.font = Font(Font.MONOSPACED, Font.PLAIN, 13)
        g2.color = Theme.fg
        val msg = "Rendering…"
        val fm = g2.fontMetrics
        g2.drawString(msg, cx - fm.stringWidth(msg) / 2, cy + r + 22)
    }
}

// ── Map canvas panel ──────────────────────────────────────────────────────────

private class MapPanel : JPanel() {
    var mapImage: BufferedImage? = null
    private var zoom = 1.0
    private var offsetX = 0
    private var offsetY = 0
    private var dragStart: Point? = null

    init {
        background = Theme.bg
        cursor = Cursor(Cursor.MOVE_CURSOR)
        addMouseWheelListener { e ->
            val factor = if (e.wheelRotation < 0) 1.1 else 0.9
            zoom = (zoom * factor).coerceIn(0.25, 4.0)
            repaint()
        }
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                dragStart = e.point
            }

            override fun mouseReleased(e: MouseEvent) {
                dragStart = null
            }
        })
        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                dragStart?.let {
                    offsetX += e.x - it.x; offsetY += e.y - it.y
                    dragStart = e.point; repaint()
                }
            }
        })
    }

    fun resetView() {
        zoom = 1.0; offsetX = 0; offsetY = 0; repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val img = mapImage ?: return
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        val w = (img.width * zoom).toInt()
        val h = (img.height * zoom).toInt()
        val x = (width - w) / 2 + offsetX
        val y = (height - h) / 2 + offsetY
        g2.drawImage(img, x, y, w, h, null)
    }
}

// ── Sidebar map list cell ─────────────────────────────────────────────────────

private class MapListCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(
        list: JList<*>, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean
    ): Component {
        val lbl = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel
        lbl.border = EmptyBorder(4, 12, 4, 12)
        lbl.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        lbl.text = value?.toString() ?: ""
        if (isSelected) {
            lbl.background = Theme.accentDim
            lbl.foreground = Theme.accent
        } else {
            lbl.background = if (index % 2 == 0) Theme.surface else Theme.bg
            lbl.foreground = Theme.fgMuted
        }
        return lbl
    }
}

// ── Status bar ────────────────────────────────────────────────────────────────

private class StatusBar : JPanel(BorderLayout()) {
    private val mapIdLabel = label("—", size = 11f, color = Theme.fg)
    private val coordsLabel = label("", size = 11f, color = Theme.fgMuted)
    private val zoomLabel = label("100%", size = 11f, color = Theme.fgMuted)

    init {
        background = Theme.surface
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.border),
            EmptyBorder(4, 12, 4, 12)
        )
        val left = JPanel(FlowLayout(FlowLayout.LEFT, 8, 0)).apply { isOpaque = false }
        left.add(label("MAP", size = 10f, bold = true, color = Theme.fgMuted))
        left.add(mapIdLabel)
        val right = JPanel(FlowLayout(FlowLayout.RIGHT, 8, 0)).apply { isOpaque = false }
        right.add(coordsLabel)
        right.add(label("|", color = Theme.border))
        right.add(zoomLabel)
        add(left, BorderLayout.WEST)
        add(right, BorderLayout.EAST)
    }

    fun update(mapId: Long, zoom: Double) {
        mapIdLabel.text = mapId.toString()
        zoomLabel.text = "${(zoom * 100).toInt()}%"
    }
}

// ── Main service ──────────────────────────────────────────────────────────────

class ChaferService(
    private val d2oService: D2OService,
    private val d2pService: D2PService,
    private val elementsService: ElementsService,
    private val mapService: MapService,
    private val worldAdapterService: WorldAdapterService,
    private val blopService: BlopService,
    private val graphicService: GraphicService,
) {
    private val renderScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun application(dofusFolder: String) {
        // ── Parse assets ──────────────────────────────────────────────────────
        val d2oEntry = d2oService.parseEntryFromFolder(dofusFolder)
        val mapScrollActionsEntry = d2oEntry[D2ODataType.MapScrollActions]!!
        val d2pEntry = d2pService.parseEntryFromFolder(dofusFolder)
        val elements = elementsService.parseElementsFromFile(
            Paths.get(dofusFolder).resolve("content").resolve("maps").resolve("elements.ele")
        )

        // ── Shared mutable state (only mutated on EDT) ────────────────────────
        var currentMap = mapService.parse(
            worldAdapterService.parseDlm(d2pEntry, 70778880, d2pService::parseDataFromEntry)
                ?: error("Map buffer could not be parsed")
        )
        var currentScroll = d2oService.parseObjectFromEntry(mapScrollActionsEntry, currentMap.id.toInt())

        // ── Build initial render ──────────────────────────────────────────────
        var currentBuffer = blopService.render(
            graphicService.createCanvas(1280, 1024),
            BlopEntry(
                d2pEntry, elements, currentMap,
                worldAdapterService.pngLoader(d2pEntry, d2pService::parseDataFromEntry),
                worldAdapterService.jpgLoader(d2pEntry, d2pService::parseDataFromEntry)
            )
        )

        // ── Look & feel ───────────────────────────────────────────────────────
        FlatDarkLaf.setup()
        UIManager.put("List.background", Theme.bg)
        UIManager.put("ScrollPane.background", Theme.bg)
        UIManager.put("ScrollBar.trackColor", Theme.bg)
        UIManager.put("ScrollBar.thumbColor", Theme.border)

        SwingUtilities.invokeLater {
            // ── Widgets ───────────────────────────────────────────────────────
            val mapPanel = MapPanel().also { it.mapImage = currentBuffer }
            val overlay = LoadingOverlay()
            val statusBar = StatusBar()

            // Map list
            val mapIds = d2pEntry.keys
                .filter { it.endsWith(".dlm") }
                .map { it.substringAfterLast("/").removeSuffix(".dlm").toInt() }
                .sorted()
                .toTypedArray()
            val listModel = DefaultListModel<Int>().also { m -> mapIds.forEach(m::addElement) }
            val mapList = JList(listModel).apply {
                selectionMode = ListSelectionModel.SINGLE_SELECTION
                cellRenderer = MapListCellRenderer()
                background = Theme.bg
                border = EmptyBorder(0, 0, 0, 0)
            }

            // Nav buttons
            val btnUp = navButton("↑")
            val btnDown = navButton("↓")
            val btnLeft = navButton("←")
            val btnRight = navButton("→")
            val btnReset = JButton("⊙ Reset View").apply {
                background = Theme.surfaceHigh; foreground = Theme.fgMuted
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.border),
                    EmptyBorder(4, 10, 4, 10)
                )
                isContentAreaFilled = false; isFocusPainted = false
                cursor = Cursor(Cursor.HAND_CURSOR)
                font = Font(Font.MONOSPACED, Font.PLAIN, 11)
                addActionListener { mapPanel.resetView() }
            }

            // ── Core render helper ────────────────────────────────────────────
            fun enableNav(enable: Boolean) {
                for (b in listOf(btnUp, btnDown, btnLeft, btnRight)) b.isEnabled = enable
            }

            fun refreshNavState() {
                val s = currentScroll
                btnUp.isEnabled = (s?.fields?.getAsInt("topMapId").let {
                    if (it != null && it > 0) it else currentMap.topNeighbourId
                }) > 0
                btnDown.isEnabled = s?.fields?.getAsInt("bottomMapId").let {
                    if (it != null && it > 0) it else currentMap.bottomNeighbourId
                } > 0
                btnLeft.isEnabled = s?.fields?.getAsInt("leftMapId").let {
                    if (it != null && it > 0) it else currentMap.leftNeighbourId
                } > 0
                btnRight.isEnabled = s?.fields?.getAsInt("rightMapId").let {
                    if (it != null && it > 0) it else currentMap.rightNeighbourId
                } > 0
            }

            fun drawFromMapId(id: Int) {
                overlay.start()
                enableNav(false)
                renderScope.launch {
                    mapPanel.mapImage = null // release old image first
                    val dlm = worldAdapterService.parseDlm(d2pEntry, id, d2pService::parseDataFromEntry)
                        ?: run {
                            withContext(Dispatchers.Swing) { overlay.stop(); enableNav(true) }
                            return@launch
                        }
                    val map = mapService.parse(dlm)
                    val buffer = blopService.render(
                        graphicService.createCanvas(1280, 1024),
                        BlopEntry(
                            d2pEntry, elements, map,
                            worldAdapterService.pngLoader(d2pEntry, d2pService::parseDataFromEntry),
                            worldAdapterService.jpgLoader(d2pEntry, d2pService::parseDataFromEntry)
                        )
                    )
                    val scroll = d2oService.parseObjectFromEntry(mapScrollActionsEntry, map.id.toInt())

                    withContext(Dispatchers.Swing) {
                        currentMap = map
                        currentScroll = scroll
                        currentBuffer = buffer
                        mapPanel.mapImage = buffer
                        mapPanel.resetView()
                        mapList.setSelectedValue(map.id.toInt(), true)
                        statusBar.update(map.id.toLong(), 1.0)
                        overlay.stop()
                        refreshNavState()
                    }
                }
            }

            // ── List interaction ──────────────────────────────────────────────
            mapList.addListSelectionListener { e ->
                if (!e.valueIsAdjusting) {
                    val id = mapList.selectedValue ?: return@addListSelectionListener
                    drawFromMapId(id)
                }
            }


            // ── Nav actions ───────────────────────────────────────────────────
            btnUp.addActionListener {
                drawFromMapId(currentScroll?.fields?.getAsInt("topMapId").let {
                    if (it != null && it > 0) it else currentMap.topNeighbourId
                })
            }
            btnDown.addActionListener {
                drawFromMapId(currentScroll?.fields?.getAsInt("bottomMapId").let {
                    if (it != null && it > 0) it else currentMap.bottomNeighbourId
                })
            }
            btnLeft.addActionListener {
                drawFromMapId(currentScroll?.fields?.getAsInt("leftMapId").let {
                    if (it != null && it > 0) it else currentMap.leftNeighbourId
                })
            }
            btnRight.addActionListener {
                drawFromMapId(currentScroll?.fields?.getAsInt("rightMapId").let {
                    if (it != null && it > 0) it else currentMap.rightNeighbourId
                })
            }

            // ── Layout ────────────────────────────────────────────────────────

            // Sidebar: search + list
            val sideHeader = JPanel(BorderLayout(0, 6)).apply {
                isOpaque = false
                border = EmptyBorder(12, 10, 8, 10)
                add(label("MAPS", size = 10f, bold = true, color = Theme.fgMuted), BorderLayout.NORTH)
            }
            val sidebar = JPanel(BorderLayout()).apply {
                background = Theme.surface
                border = BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.border)
                preferredSize = Dimension(200, 0)
                add(sideHeader, BorderLayout.NORTH)
                add(JScrollPane(mapList).apply {
                    border = null
                    background = Theme.bg
                    viewport.background = Theme.bg
                    verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                    horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                }, BorderLayout.CENTER)
            }

            // Nav panel: compass rose
            val compass = JPanel(GridBagLayout()).apply {
                isOpaque = false
                val c = GridBagConstraints().apply { insets = Insets(3, 3, 3, 3) }
                fun place(btn: JButton, gx: Int, gy: Int) {
                    c.gridx = gx; c.gridy = gy; add(btn, c)
                }
                place(btnUp, 1, 0)
                place(btnLeft, 0, 1)
                place(btnRight, 2, 1)
                place(btnDown, 1, 2)
            }
            val navPanel = JPanel(BorderLayout(0, 8)).apply {
                background = Theme.surface
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 1, 0, 0, Theme.border),
                    EmptyBorder(16, 12, 12, 12)
                )
                preferredSize = Dimension(160, 0)
                add(label("NAVIGATE", size = 10f, bold = true, color = Theme.fgMuted), BorderLayout.NORTH)
                add(compass, BorderLayout.CENTER)
                val bottomPanel = JPanel(BorderLayout(0, 6)).apply {
                    isOpaque = false
                    add(btnReset, BorderLayout.CENTER)

                    // Map info box
                    val infoBox = JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.Y_AXIS)
                        isOpaque = false
                        border = BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Theme.border),
                            EmptyBorder(8, 8, 8, 8)
                        )
                        add(label("CURRENT MAP", size = 9f, bold = true, color = Theme.fgMuted))
                        add(Box.createVerticalStrut(4))
                        val idLabel = label(currentMap.id.toString(), size = 11f, color = Theme.accent)
                        add(idLabel)
                    }
                    add(infoBox, BorderLayout.SOUTH)
                }
                add(bottomPanel, BorderLayout.SOUTH)
            }

            // Map viewport with overlay stacked
            val viewport = object : JLayeredPane() {
                override fun doLayout() {
                    // Called by Swing's layout pass, guaranteed to run even on first paint
                    for (c in components) c.setBounds(0, 0, width, height)
                }
            }.apply {
                add(mapPanel, JLayeredPane.DEFAULT_LAYER)
                add(overlay, JLayeredPane.POPUP_LAYER)
            }

            // Root
            val root = JPanel(BorderLayout()).apply {
                background = Theme.bg
                add(sidebar, BorderLayout.WEST)
                add(viewport, BorderLayout.CENTER)
                add(navPanel, BorderLayout.EAST)
                add(statusBar, BorderLayout.SOUTH)
            }

            // ── Frame ─────────────────────────────────────────────────────────
            JFrame("Chafer — Map Visualizer").apply {
                defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                contentPane = root
                minimumSize = Dimension(900, 600)
                pack()
                setLocationRelativeTo(null)
                extendedState = JFrame.MAXIMIZED_BOTH

                addComponentListener(object : ComponentAdapter() {
                    override fun componentResized(e: ComponentEvent) {
                        root.setSize(e.component.size.width, e.component.size.height)
                        root.revalidate()
                        root.repaint()
                    }
                })

                isVisible = true

                validate() // ← force a full layout pass after maximized state is applied
            }

            // Initial state
            statusBar.update(currentMap.id.toLong(), 1.0)
            refreshNavState()
        }
    }
}