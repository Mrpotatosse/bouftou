package services

import const.AtouinConstants
import dlm.entities.Cell
import dlm.entities.Layer
import dlm.entities.elements.BasicElement
import dlm.entities.elements.GraphicalElement
import ele.entities.GraphicalElementType
import ele.entities.subtypes.NormalGraphicalElementData
import entities.BlopEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.time.DurationUnit
import kotlin.time.measureTime

class LayerRenderService(
    private val graphicService: GraphicService
) : ParamsRenderService<BufferedImage, BlopEntry> {
    override fun render(raw: BufferedImage, params: BlopEntry): BufferedImage = runBlocking {
        val layerImages = params.dofusMap.layers
            .map { layer ->
                async(Dispatchers.Default) {
                    val layerBuffer = graphicService.createCanvas(
                        raw.width,
                        raw.height
                    )

                    val layerGraphic = graphicService.getGraphic(layerBuffer)

                    try {
                        val layerRenderDur = measureTime { renderLayer(params, layerGraphic, layer) }
                        println("               Layer RenderDuration: ${layerRenderDur.toString(DurationUnit.MILLISECONDS)}")
                        layerBuffer
                    } finally {
                        layerGraphic.dispose()
                    }
                }
            }
            .awaitAll()

        val graphic = graphicService.getGraphic(raw)

        try {
            layerImages.forEach {
                graphic.drawImage(it, 0, 0, null)
            }
        } finally {
            graphic.dispose()
        }

        raw
    }

    private fun renderLayer(params: BlopEntry, graphic: Graphics2D, layer: Layer) {
        layer.cells.forEach {
            renderCell(params, graphic, it)
        }
    }

    private fun renderCell(params: BlopEntry, graphic: Graphics2D, cell: Cell) {
        val col = cell.cellId % AtouinConstants.MAP_WIDTH
        val row = cell.cellId / AtouinConstants.MAP_WIDTH
        val cellX = col * AtouinConstants.CELL_WIDTH + if (row % 2 == 1) AtouinConstants.CELL_HALF_WIDTH else 0.0
        val cellY = row * AtouinConstants.CELL_HALF_HEIGHT

        return cell.elements.forEach {
            renderElement(params, graphic, it, cellX, cellY)
        }
    }

    private fun renderElement(
        params: BlopEntry,
        graphic: Graphics2D,
        element: BasicElement,
        cellX: Double,
        cellY: Double
    ) {
        val ge = element as? GraphicalElement ?: return

        val (_, graphicalAny) = params.elements.data[ge.elementId.toInt()] ?: return
        val graphical = graphicalAny as? NormalGraphicalElementData ?: return

        if (graphical.type != GraphicalElementType.NORMAL) return

        val gfx = if (params.elements.isJpg.contains(graphical.gfxId)) {
            params.jpgLoader.invoke(graphical.gfxId)
        } else {
            params.pngLoader.invoke(graphical.gfxId)
        } ?: return

        val originX = cellX + (-graphical.origin.x + AtouinConstants.CELL_HALF_WIDTH + ge.pixelOffset.x)
        val originY =
            cellY + (-graphical.origin.y + AtouinConstants.CELL_HALF_HEIGHT - ge.altitude * 10.0 + ge.pixelOffset.y)

        val cm = ge.colorMultiplicator

        val prepared = graphicService.buildGfxImage(
            gfx,
            graphical.horizontalSymmetry,
            cm.red / 255.0,
            cm.green / 255.0,
            cm.blue / 255.0
        )

        // 🔥 Only set composite once per frame if possible (huge win)
        graphic.composite = AlphaComposite.SrcOver

        graphic.drawImage(prepared, originX.toInt(), originY.toInt(), null)
    }
}