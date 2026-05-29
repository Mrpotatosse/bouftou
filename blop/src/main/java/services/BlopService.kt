package services

import entities.BlopEntry
import java.awt.image.BufferedImage

class BlopService(
    private val graphicService: GraphicService,
    private val backgroundRenderService: BackgroundRenderService,
    private val layerRenderService: LayerRenderService,
    private val foregroundRenderService: ForegroundRenderService
) : ParamsRenderService<BufferedImage, BlopEntry> {
    override fun render(raw: BufferedImage, params: BlopEntry): BufferedImage {
        return raw
            .let { backgroundRenderService.render(it, params) }
            .let { layerRenderService.render(it, params) }
            .let { foregroundRenderService.render(it, params) }
            .let { graphicService.fillBackground(it, params.dofusMap.backgroundColor) }
    }
}