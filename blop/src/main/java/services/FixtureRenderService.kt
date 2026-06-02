package services

import const.AtouinConstants
import dlm.entities.Fixture
import entities.BlopEntry
import entities.PreparedFixture
import kotlinx.coroutines.*
import java.awt.AlphaComposite
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

class FixtureRenderService(
    private val graphicService: GraphicService
) {

    fun render(
        raw: BufferedImage,
        params: BlopEntry,
        fixtures: Collection<Fixture>,
    ): BufferedImage = runBlocking {
        // 1. Load and process all fixtures in parallel (load + color multiply)
        val prepared: List<PreparedFixture> = coroutineScope {
            fixtures.map { fixture ->
                async(Dispatchers.Default) {
                    val gfx = if (params.elements.isJpg.contains(fixture.fixtureId))
                        params.jpgLoader.invoke(fixture.fixtureId) ?: return@async null
                    else
                        params.pngLoader.invoke(fixture.fixtureId) ?: return@async null

                    val imgW = gfx.width.toDouble()
                    val imgH = gfx.height.toDouble()
                    val halfW = imgW * 0.5
                    val halfH = imgH * 0.5

                    // 2. Precompute multipliers (pure math, do it once per fixture)
                    val redMul = (fixture.redMultiplier / 127.0 + 1.0).coerceIn(0.0, 1.0)
                    val greenMul = (fixture.greenMultiplier / 127.0 + 1.0).coerceIn(0.0, 1.0)
                    val blueMul = (fixture.blueMultiplier / 127.0 + 1.0).coerceIn(0.0, 1.0)
                    val alphaMul = (fixture.alpha.toFloat() / 255f).coerceIn(0f, 1f)

                    val rotation = fixture.rotation / 100.0 * (Math.PI / 180.0)
                    val tx = AffineTransform().apply {
                        translate(
                            (fixture.offset.x + AtouinConstants.CELL_HALF_WIDTH) + halfW,
                            (fixture.offset.y + AtouinConstants.CELL_HEIGHT) + halfH
                        )
                        rotate(rotation)
                        scale(fixture.xScale / 1000.0, fixture.yScale / 1000.0)
                        translate(-halfW, -halfH)
                    }

                    // 3. Heavy per-pixel work happens here, in parallel
                    val colorized = graphicService.buildGfxImage(gfx, false, redMul, greenMul, blueMul)

                    PreparedFixture(colorized, tx, alphaMul)
                }
            }.awaitAll().filterNotNull()
        }

        // 4. Sequential compositing — Graphics2D is NOT thread-safe
        val graphic = graphicService.getGraphic(raw)
        for (pf in prepared) {
            graphic.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pf.alpha)
            graphic.drawImage(pf.image, pf.transform, null)
            pf.image.flush() // 5. Free immediately after draw, not after the full loop
        }
        graphic.dispose()

        raw
    }
}