package services

import const.AtouinConstants
import dlm.entities.Fixture
import entities.BlopEntry
import java.awt.AlphaComposite
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

class FixtureRenderService(
    private val graphicService: GraphicService
) {
    fun render(raw: BufferedImage, params: BlopEntry, fixtures: Collection<Fixture>): BufferedImage {
        val graphic = graphicService.getGraphic(raw)

        for (fixture in fixtures) {
            val gfx = params.pngLoader.invoke(fixture.fixtureId) ?: continue

            val imgW = gfx.width.toDouble()
            val imgH = gfx.height.toDouble()
            val halfW = imgW * 0.5
            val halfH = imgH * 0.5

            val scaleX = fixture.xScale / 1000.0
            val scaleY = fixture.yScale / 1000.0
            val rotation = fixture.rotation / 100.0 * (Math.PI / 180.0)

            val tx = AffineTransform().apply {
                translate(
                    (fixture.offset.x + AtouinConstants.CELL_HALF_WIDTH) + halfW,
                    (fixture.offset.y + AtouinConstants.CELL_HEIGHT) + halfH
                )
                rotate(rotation)
                scale(scaleX, scaleY)
                translate(-halfW, -halfH)
            }

            graphic.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
            graphic.drawImage(gfx, tx, null)
        }

        graphic.dispose()
        return raw
    }
}