package services

import adapter.services.WorldAdapterService
import com.formdev.flatlaf.FlatLightLaf
import d2p.services.D2PService
import dlm.services.MapService
import ele.services.ElementsService
import entities.BlopEntry
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.nio.file.Paths
import javax.swing.*


class ChaferService(
    private val d2pService: D2PService,
    private val elementsService: ElementsService,
    private val mapService: MapService,
    private val worldAdapterService: WorldAdapterService,
    private val blopService: BlopService,
    private val graphicService: GraphicService,
) {
    fun application(dofusFolder: String) {
        val d2pEntry = d2pService.parseEntryFromFolder(dofusFolder)
        val elements = elementsService.parseElementsFromFile(
            Paths.get(dofusFolder)
                .resolve("content")
                .resolve("maps")
                .resolve("elements.ele")
        )

        var currentMap = mapService.parse(
            worldAdapterService.parseDlm(d2pEntry, 84674563, d2pService::parseDataFromEntry)
                ?: error("Map buffer could not be parsed")
        )

        var currentMapBuffer = blopService.render(
            graphicService.createCanvas(1280, 1024), BlopEntry(
                d2pEntry, elements, currentMap,
                worldAdapterService.pngLoader(d2pEntry, d2pService::parseDataFromEntry),
                worldAdapterService.jpgLoader(d2pEntry, d2pService::parseDataFromEntry)
            )
        )

        FlatLightLaf.setup()
        SwingUtilities.invokeLater {
            val frame = JFrame("Dofus 2.39.0")
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            val mapPanel = object : JPanel() {
                init {
                    background = Color.BLACK
                }

                override fun paintComponent(g: Graphics) {
                    super.paintComponent(g)
                    val x = (width - currentMapBuffer.width) / 2
                    val y = (height - currentMapBuffer.height) / 2
                    g.drawImage(currentMapBuffer, x, y, null)
                }

                override fun getPreferredSize() = Dimension(currentMapBuffer.width, currentMapBuffer.height)
            }

            // Map list
            val mapIds = d2pEntry.keys
                .filter { it.endsWith(".dlm") }
                .map { it.substringAfterLast("/").removeSuffix(".dlm").toInt() }
            val mapList = JList(mapIds.toTypedArray())
            mapList.selectionMode = ListSelectionModel.SINGLE_SELECTION
            mapList.selectedIndex = 0
            mapList.addListSelectionListener { e ->
                if (!e.valueIsAdjusting) {
                    val selectedId = mapList.selectedValue ?: return@addListSelectionListener
                    currentMap = mapService.parse(
                        worldAdapterService.parseDlm(d2pEntry, selectedId, d2pService::parseDataFromEntry)
                            ?: return@addListSelectionListener
                    )
                    currentMapBuffer = blopService.render(
                        graphicService.createCanvas(1280, 1024), BlopEntry(
                            d2pEntry,
                            elements,
                            currentMap,
                            worldAdapterService.pngLoader(d2pEntry, d2pService::parseDataFromEntry),
                            worldAdapterService.jpgLoader(d2pEntry, d2pService::parseDataFromEntry)
                        )
                    )
                    mapPanel.repaint()
                }
            }

            val scrollPane = JScrollPane(mapList).apply {
                preferredSize = Dimension(200, 0)
                border = BorderFactory.createTitledBorder("Maps")
            }

            // Root layout
            val root = JPanel(BorderLayout())
            root.add(scrollPane, BorderLayout.WEST)
            root.add(mapPanel, BorderLayout.CENTER)

            frame.contentPane = root
            frame.pack()
            frame.setLocationRelativeTo(null)
            frame.extendedState = JFrame.MAXIMIZED_BOTH
            frame.minimumSize = Dimension(currentMapBuffer.width, currentMapBuffer.height)
            frame.isVisible = true
        }
    }
}