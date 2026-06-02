package layouts

import components.MapComponent
import entities.UITheme
import services.components.MapComponentService
import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel

class Main(
    private val mapComponentService: MapComponentService
) : JFrame("Chafer — Map Visualizer") {

    private val defaultMiddleDimension = Dimension(1280, 1024)
    private val ratio =
        defaultMiddleDimension.width.toDouble() / defaultMiddleDimension.height.toDouble()  // change to your desired ratio e.g. 16/9

    private val gbc = GridBagConstraints().apply {
        gridy = 0
        weighty = 1.0
    }
    private val leftPanel = JPanel().apply {
        background = UITheme.bg
        preferredSize = Dimension(0, 0)
    }
    private val rightPanel = JPanel().apply {
        background = UITheme.bg  // temporary
        preferredSize = Dimension(0, 0)
    }
    private lateinit var mapGetter: (Int) -> BufferedImage
    fun openWindow(fn: (Int) -> BufferedImage) {
        mapGetter = fn
        defaultCloseOperation = EXIT_ON_CLOSE
        minimumSize = defaultMiddleDimension

        val root = JPanel(GridBagLayout())

        // left — grows, stretches fully
        gbc.gridx = 0
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.BOTH
        root.add(leftPanel, gbc)

        // map — fixed ratio, centered vertically, never stretched
        gbc.gridx = 1
        gbc.weightx = 0.0
        gbc.fill = GridBagConstraints.NONE  // ← key: don't stretch it
        gbc.anchor = GridBagConstraints.CENTER


        root.add(wrapMap(mapComponentService.component(mapGetter(84674563))), gbc)

        // right — grows, stretches fully
        gbc.gridx = 2
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.BOTH
        root.add(rightPanel, gbc)

        contentPane = root  // ← directly, no JLayeredPane wrapper

        pack()
        setLocationRelativeTo(null)
        extendedState = MAXIMIZED_BOTH
        isVisible = true
    }

    fun wrapMap(map: MapComponent): JPanel {
        return object : JPanel(BorderLayout()) {
            override fun getPreferredSize(): Dimension {
                val h = parent?.height?.takeIf { it > 0 } ?: 600
                return Dimension((h * ratio).toInt(), h)
            }

            override fun getMinimumSize() = preferredSize
            override fun getMaximumSize() = preferredSize
        }.apply {
            background = Color.GREEN
            add(map, BorderLayout.CENTER)
        }
    }
}