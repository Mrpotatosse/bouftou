package services

import entities.BlopEntry
import extensions.profile
import java.awt.image.BufferedImage

class BlopService(
    private val graphicService: GraphicService,
    private val backgroundRenderService: BackgroundRenderService,
    private val layerRenderService: LayerRenderService,
    private val foregroundRenderService: ForegroundRenderService
) : ParamsRenderService<BufferedImage, BlopEntry> {
    override fun render(raw: BufferedImage, params: BlopEntry): BufferedImage {
        println("┌───────────────────────────────────┐")
        println("│ Render Profiling  %-14s  │".format(params.dofusMap.id))
        println("├────────────────────┬──────────────┤")
        val result = raw
            .profile("Background") { backgroundRenderService.render(it, params) }
            .profile("Layer") { layerRenderService.render(it, params) }
            .profile("Foreground") { foregroundRenderService.render(it, params) }
            .profile("Fill background") { graphicService.fillBackground(it, params.dofusMap.backgroundColor) }
        println("└────────────────────┴──────────────┘")
        return result
    }
}