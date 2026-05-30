package services

import const.AtouinConstants
import dlm.entities.Cell
import dlm.entities.Layer
import dlm.entities.elements.BasicElement
import dlm.entities.elements.GraphicalElement
import ele.entities.GraphicalElementType
import ele.entities.subtypes.NormalGraphicalElementData
import entities.BlopEntry
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class LayerRenderService(
    private val graphicService: GraphicService
) : ParamsRenderService<BufferedImage, BlopEntry> {
    override fun render(raw: BufferedImage, params: BlopEntry): BufferedImage {
        val graphic = graphicService.getGraphic(raw)
        for (layer in params.dofusMap.layers) {
            renderLayer(params, graphic, layer)
        }
        graphic.dispose()
        return raw
    }

    private fun renderLayer(params: BlopEntry, graphic: Graphics2D, layer: Layer) =
        layer.cells.forEach {
            renderCell(params, graphic, it)
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
        if (element !is GraphicalElement) return
        val data = params.elements.data[element.elementId.toInt()] ?: return
        val graphical = data.second
        if (graphical !is NormalGraphicalElementData) return
        when (graphical.type) {
            GraphicalElementType.NORMAL -> {
                val gfx =
                    if (params.elements.isJpg.contains(graphical.gfxId)) params.jpgLoader.invoke(graphical.gfxId)
                        ?: return
                    else params.pngLoader.invoke(graphical.gfxId) ?: return

                val originOffsetX = -graphical.origin.x
                val originOffsetY = -graphical.origin.y

                val dataX = originOffsetX + (AtouinConstants.CELL_HALF_WIDTH + element.pixelOffset.x)
                val dataY =
                    originOffsetY + (AtouinConstants.CELL_HALF_HEIGHT - element.altitude * 10.0 + element.pixelOffset.y)

                val cm = element.colorMultiplicator
                
                val prepared = graphicService.buildGfxImage(
                    gfx,
                    graphical.horizontalSymmetry,
                    (cm.red / 255.0f).toFloat(),
                    (cm.green / 255.0f).toFloat(),
                    (cm.blue / 255.0f).toFloat(),
                )
                graphic.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
                graphic.drawImage(
                    prepared,
                    (cellX + dataX).toInt(),
                    (cellY + dataY).toInt(),
                    null
                )
            }

            GraphicalElementType.BLENDED -> {
                println("blended $element ${element.elementId}")
            }

            GraphicalElementType.BOUNDING_BOX -> {
                println("bounding box $element ${element.elementId}")
            }

            GraphicalElementType.ANIMATED -> {
                println("animated $element ${element.elementId}")
            }

            else -> {}
        }


    }
}