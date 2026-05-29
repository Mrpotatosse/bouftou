package services

import entities.BlopEntry
import java.awt.image.BufferedImage

class BackgroundRenderService(
    private val fixtureRenderService: FixtureRenderService
) : ParamsRenderService<BufferedImage, BlopEntry> {
    override fun render(raw: BufferedImage, params: BlopEntry): BufferedImage {
        return fixtureRenderService.render(raw, params, params.dofusMap.backgroundFixtures)
    }
}