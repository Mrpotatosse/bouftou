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
import java.util.concurrent.ConcurrentHashMap


class LayerRenderService(
    private val graphicService: GraphicService,
    // ✅ Injected so the cache survives across renders — pass a shared instance from outside,
    //    or leave the default for a per-service cache.
    private val gfxCache: ConcurrentHashMap<String, BufferedImage> = ConcurrentHashMap()
) : ParamsRenderService<BufferedImage, BlopEntry> {

    override fun render(raw: BufferedImage, params: BlopEntry): BufferedImage {
        val graphic = graphicService.getGraphic(raw)

        try {
            // ✅ Composite and clip set once — outside every loop
            graphic.composite = AlphaComposite.SrcOver

            params.dofusMap.layers.forEach { layer ->
                renderLayer(params, graphic, layer)
            }
        } finally {
            graphic.dispose()
        }

        return raw
        // ✅ No coroutines: AWT Graphics2D is NOT thread-safe — parallel writes to the same
        //    raster corrupt pixels. Sequential rendering on one Graphics2D is both correct and
        //    faster here because there is no IO to overlap (images are already loaded).
    }

    private fun renderLayer(params: BlopEntry, graphic: Graphics2D, layer: Layer) {
        layer.cells.forEach { renderCell(params, graphic, it) }
    }

    private fun renderCell(params: BlopEntry, graphic: Graphics2D, cell: Cell) {
        val col = cell.cellId % AtouinConstants.MAP_WIDTH
        val row = cell.cellId / AtouinConstants.MAP_WIDTH
        val cellX = col * AtouinConstants.CELL_WIDTH +
                if (row % 2 == 1) AtouinConstants.CELL_HALF_WIDTH else 0.0
        val cellY = row * AtouinConstants.CELL_HALF_HEIGHT

        cell.elements.forEach { renderElement(params, graphic, it, cellX, cellY) }
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

        val cm = ge.colorMultiplicator

        // ✅ Cache key encodes every variable that affects the output pixel data
        val cacheKey = "${graphical.gfxId}_${graphical.horizontalSymmetry}_${cm.red}_${cm.green}_${cm.blue}"

        val prepared = gfxCache.getOrPut(cacheKey) {
            // Loader is only invoked on cache miss
            val gfx = if (params.elements.isJpg.contains(graphical.gfxId)) {
                params.jpgLoader.invoke(graphical.gfxId)
            } else {
                params.pngLoader.invoke(graphical.gfxId)
            } ?: return   // ✅ returns from renderElement, not just the lambda

            graphicService.buildGfxImage(
                gfx,
                graphical.horizontalSymmetry,
                cm.red / 255.0,
                cm.green / 255.0,
                cm.blue / 255.0
            )
        }

        val originX = cellX + (-graphical.origin.x + AtouinConstants.CELL_HALF_WIDTH + ge.pixelOffset.x)
        val originY =
            cellY + (-graphical.origin.y + AtouinConstants.CELL_HALF_HEIGHT - ge.altitude * 10.0 + ge.pixelOffset.y)

        graphic.drawImage(prepared, originX.toInt(), originY.toInt(), null)
    }
}