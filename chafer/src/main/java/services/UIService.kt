package services

import adapter.services.WorldAdapterService
import d2o.entities.D2ODataType
import d2o.services.D2OService
import d2p.services.D2PService
import dlm.services.MapService
import ele.services.ElementsService
import entities.BlopEntry
import extensions.freeIfDirect
import functions.memoryStats
import layouts.Main
import java.awt.image.BufferedImage
import java.nio.file.Paths

class UIService(
    private val d2oService: D2OService,
    private val d2pService: D2PService,
    private val elementsService: ElementsService,
    private val mapService: MapService,
    private val worldAdapterService: WorldAdapterService,
    private val blopService: BlopService,
    private val graphicService: GraphicService,
    private val main: Main
) {
    fun application(dofusFolder: String) {
        // ── Parse assets ──────────────────────────────────────────────────────
        val d2oEntry = d2oService.parseEntryFromFolderAsStore(dofusFolder)
        val mapScrollActionsEntry = d2oEntry.get(D2ODataType.MapScrollActions)!!
        val d2pEntry = d2pService.parseEntryFromFolderAsStore(dofusFolder)
        val elements = elementsService.parseElementsFromFile(
            Paths.get(dofusFolder).resolve("content").resolve("maps").resolve("elements.ele")
        )

        fun getMap(id: Int): BufferedImage {
            println(memoryStats("Before map getter"))
            val dlm = worldAdapterService.parseDlm(d2pEntry, id, d2pService::parseDataFromEntry)
                ?: throw IllegalArgumentException("Map DLM not found with id : $id")

            val map = mapService.parse(dlm)
            println(memoryStats("Before map render"))
            dlm.freeIfDirect()
            val result = blopService.render(
                graphicService.createCanvas(1280, 1024),
                BlopEntry(
                    d2pEntry, elements, map,
                    worldAdapterService.pngLoader(d2pEntry, d2pService::parseDataFromEntry),
                    worldAdapterService.jpgLoader(d2pEntry, d2pService::parseDataFromEntry)
                )
            )
            println(memoryStats("After map render"))
            return result
        }

        // ── Look & feel ───────────────────────────────────────────────────────
        /*FlatDarkLaf.setup()
        UIManager.put("List.background", UITheme.bg)
        UIManager.put("ScrollPane.background", UITheme.bg)
        UIManager.put("ScrollBar.trackColor", UITheme.bg)
        UIManager.put("ScrollBar.thumbColor", UITheme.border)

        SwingUtilities.invokeLater {
            main.openWindow {
                getMap(it)
            }
        }*/
    }
}