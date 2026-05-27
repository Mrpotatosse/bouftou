package render.services

import const.AtouinConstants
import dlm.entities.Fixture
import dlm.entities.Map
import dlm.entities.elements.ElementType
import dlm.entities.elements.GraphicalElement
import ele.entities.Elements
import ele.entities.subtypes.NormalGraphicalElementData
import render.entities.CanvasDraw
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class RenderService {
    fun drawMap(
        map: Map,
        jpgLoader: (Int) -> BufferedImage?,
        pngLoader: (Int) -> BufferedImage?,
        elements: Elements
    ): ByteArray {
        val cellW = AtouinConstants.CELL_WIDTH        // 86
        val cellHW = AtouinConstants.CELL_HALF_WIDTH   // 43
        val cellH = AtouinConstants.CELL_HEIGHT       // 60
        val cellHH = AtouinConstants.CELL_HALF_HEIGHT  // 30
        val mapW = AtouinConstants.MAP_WIDTH         // 14
        val mapH = AtouinConstants.MAP_HEIGHT        // 20

        // val canvasW = 1920 * 4
        // val canvasH = 1080 * 4
        // val originX = centerW
        // val originY = centerH

        val bgCmds = mutableListOf<CanvasDraw>()  // background fixtures
        val mapCmds = mutableListOf<CanvasDraw>()  // layer elements
        val fgCmds = mutableListOf<CanvasDraw>()  // foreground fixtures

        // draw
        // ── Helper: build fixture draw command ─────────────────────────────────
        // Mirrors MapRenderer.as drawFixtures() lines 1402–1418:
        //
        //   m.translate(-halfW, -halfH)
        //   m.scale(xScale / 1000, yScale / 1000)
        //   m.rotate(rotation / 100 * PI / 180)
        //   m.translate(offset.x + CELL_HALF_WIDTH, offset.y + CELL_HEIGHT)
        //
        //   colorTransform:
        //     red   = redMultiplier   / 127 + 1    ← note: /127, not /255
        //     green = greenMultiplier / 127 + 1
        //     blue  = blueMultiplier  / 127 + 1
        //     alpha = alpha           / 255
        fun fixtureCmd(f: Fixture, dest: MutableList<CanvasDraw>) {
            val gfx = pngLoader.invoke(f.fixtureId) ?: return

            val w = gfx.width.toDouble()
            val h = gfx.height.toDouble()
            val halfW = w * 0.5
            val halfH = h * 0.5

            val scaleX = f.xScale / 1000.0
            val scaleY = f.yScale / 1000.0
            val rotRad = f.rotation / 100.0 * (Math.PI / 180.0)

            // Build the composite transform (same order as AS3 Matrix calls).
            // We store the final top-left draw position separately so the
            // two-pass bounds tracking can use it without running the full transform.
            //
            // After all transforms, the sprite's registration point lands at:
            //   tx = offset.x + CELL_HALF_WIDTH
            //   ty = offset.y + CELL_HEIGHT
            //
            // In Java2D we centre-transform the image then translate to that point.
            val tx = f.offset.x + cellHW
            val ty = (f.offset.y + cellH).toDouble()

            val at = AffineTransform()
            at.translate(tx, ty)
            at.rotate(rotRad)
            at.scale(scaleX, scaleY)
            at.translate(-halfW, -halfH)

            val redMul = (f.redMultiplier / 127.0 + 1.0).toFloat().coerceIn(0f, 4f)
            val greenMul = (f.greenMultiplier / 127.0 + 1.0).toFloat().coerceIn(0f, 4f)
            val blueMul = (f.blueMultiplier / 127.0 + 1.0).toFloat().coerceIn(0f, 4f)
            val alphaMul = (f.alpha / 255.0).toFloat().coerceIn(0f, 1f)

            // Approximate bounding box for canvas sizing:
            // use the transform to map the four corners of the image.
            val corners = arrayOf(
                doubleArrayOf(0.0, 0.0),
                doubleArrayOf(w, 0.0),
                doubleArrayOf(w, h),
                doubleArrayOf(0.0, h),
            )
            val mapped = corners.map { (cx, cy) ->
                val pt = doubleArrayOf(cx, cy)
                at.transform(pt, 0, pt, 0, 1)
                pt
            }
            val minX = mapped.minOf { it[0] }.toInt()
            val minY = mapped.minOf { it[1] }.toInt()

            dest.add(
                CanvasDraw(
                    img = gfx,
                    x = minX,
                    y = minY,
                    transform = at,
                    redMul = redMul,
                    greenMul = greenMul,
                    blueMul = blueMul,
                    alphaMul = alphaMul,
                )
            )
        }

        // ── 1. Background fixtures ─────────────────────────────────────────────
        for (f in map.backgroundFixtures) fixtureCmd(f, bgCmds)


        // ── 2. Layer / cell / element loop ────────────────────────────────────
        for (layer in map.layers) {
            for (cell in layer.cells) {
                val col = cell.cellId % mapW
                val row = cell.cellId / mapW
                val cellX = col * cellW + (if (row % 2 == 1) cellHW else 0).toInt()
                val cellY = row * cellHH

                for (element in cell.elements) {
                    if (element.type != ElementType.GRAPHICAL) continue
                    val ge = element as? GraphicalElement ?: continue

                    val ed = elements.data[ge.elementId.toInt()]

                    val typedEd = ed?.second as? NormalGraphicalElementData ?: continue
                    val gfx = pngLoader.invoke(typedEd.gfxId) ?: continue

                    // Position — mirrors addCellBitmapsElements:
                    //   data.x = -ged.origin.x
                    //   data.y = -ged.origin.y
                    //   data.x += CELL_HALF_WIDTH  + ge.pixelOffset.x
                    //   data.y += CELL_HALF_HEIGHT - altitude*10 + ge.pixelOffset.y
                    //   draw at (cellX + data.x, cellY + data.y)
                    val dataX = -typedEd.origin.x + cellHW + ge.pixelOffset.x
                    val dataY = -typedEd.origin.y + cellHH - ge.altitude * 10.0 + ge.pixelOffset.y

                    // Color — mirrors drawCellOnGroundBitmap:
                    //   colorTransform.red/green/blue = ge.colorMultiplicator.channel / 255
                    //   (finalTeint channels are [0,512]; identity = 255 → multiplier 1.0)
                    val cm = ge.colorMultiplicator
                    mapCmds.add(
                        CanvasDraw(
                            img = gfx,
                            x = (cellX + dataX).toInt(),
                            y = (cellY + dataY).toInt(),
                            flipH = typedEd.horizontalSymmetry,
                            redMul = (cm.red / 255.0).toFloat(),
                            greenMul = (cm.green / 255.0).toFloat(),
                            blueMul = (cm.blue / 255.0).toFloat(),
                        )
                    )
                }
            }
        }

        // ── 3. Foreground fixtures ─────────────────────────────────────────────
        for (f in map.foregroundFixtures) fixtureCmd(f, fgCmds)

        val allCmds = bgCmds + mapCmds + fgCmds
        // ── Pass 1: compute canvas bounds ──────────────────────────────────────
        var minX = 0
        var minY = 0
        var maxX = mapW * cellW + cellHW
        var maxY = mapH * cellHH + cellHH

        for (cmd in allCmds) {
            minX = minOf(minX, cmd.x)
            minY = minOf(minY, cmd.y)
            maxX = maxOf(maxX, cmd.x + cmd.img.width.toDouble())
            maxY = maxOf(maxY, (cmd.y + cmd.img.height).toDouble())
        }

        val canvasW = (maxX - minX).toInt()
        val canvasH = (maxY - minY).toInt()
        val originX = -minX
        val originY = -minY

        val centerW = canvasW / 2
        val centerH = canvasH / 2
        // end
        // ── Canvas + background colour ─────────────────────────────────────────
        val canvas = BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB)
        val g = canvas.createGraphics().apply {
            setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }

        val bgRaw = map.backgroundColor
        val bgR = ((bgRaw shr 16) and 0xFF).toInt()
        val bgG = ((bgRaw shr 8) and 0xFF).toInt()
        val bgB = ((bgRaw) and 0xFF).toInt()
        g.color = if (bgRaw == 0L) Color(0x2B, 0x2B, 0x2B) else Color(bgR, bgG, bgB)
        g.fillRect(0, 0, canvasW, canvasH)

        // ── Pass 2: blit in order — bg fixtures → elements → fg fixtures ───────
        fun blit(cmd: CanvasDraw) {
            val prepared = buildGfxImage(
                source = cmd.img,
                flipHorizontally = cmd.flipH,
                colorTransformRed = cmd.redMul,
                colorTransformGreen = cmd.greenMul,
                colorTransformBlue = cmd.blueMul,
            )

            g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cmd.alphaMul)

            if (cmd.transform != null) {
                // Fixture: apply the pre-built AffineTransform shifted by our origin offset.
                val shifted = AffineTransform.getTranslateInstance(originX.toDouble(), originY.toDouble())
                shifted.concatenate(cmd.transform)
                g.drawImage(prepared, shifted, null)
            } else {
                g.drawImage(prepared, cmd.x + originX, cmd.y + originY, null)
            }
        }

        for (cmd in bgCmds) blit(cmd)
        for (cmd in mapCmds) blit(cmd)
        for (cmd in fgCmds) blit(cmd)

        g.dispose()

        // ── Encode PNG ─────────────────────────────────────────────────────────
        val out = ByteArrayOutputStream()
        ImageIO.write(canvas, "PNG", out)
        return out.toByteArray()
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private fun buildGfxImage(
        source: BufferedImage,
        flipHorizontally: Boolean,
        colorTransformRed: Float,
        colorTransformGreen: Float,
        colorTransformBlue: Float,
    ): BufferedImage {
        var img = if (source.type == BufferedImage.TYPE_INT_ARGB) source
        else convertToArgb(source)

        if (flipHorizontally) {
            val w = img.width
            val h = img.height
            val tx = AffineTransform.getScaleInstance(-1.0, 1.0)
            tx.translate(-w.toDouble(), 0.0)
            img = AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
                .filter(img, BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB))
        }

        val isIdentity = colorTransformRed == 1f && colorTransformGreen == 1f && colorTransformBlue == 1f
        if (!isIdentity) {
            img = applyColorTransform(img, colorTransformRed, colorTransformGreen, colorTransformBlue)
        }

        return img
    }

    private fun convertToArgb(src: BufferedImage): BufferedImage {
        val out = BufferedImage(src.width, src.height, BufferedImage.TYPE_INT_ARGB)
        val g = out.createGraphics()
        g.drawImage(src, 0, 0, null)
        g.dispose()
        return out
    }

    private fun applyColorTransform(
        src: BufferedImage,
        redMul: Float,
        greenMul: Float,
        blueMul: Float,
    ): BufferedImage {
        val w = src.width
        val h = src.height
        val out = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val srcRaster = src.raster
        val dstRaster = out.raster
        val pixel = IntArray(4)
        for (y in 0 until h) {
            for (x in 0 until w) {
                srcRaster.getPixel(x, y, pixel)
                pixel[0] = (pixel[0] * redMul).toInt().coerceIn(0, 255)
                pixel[1] = (pixel[1] * greenMul).toInt().coerceIn(0, 255)
                pixel[2] = (pixel[2] * blueMul).toInt().coerceIn(0, 255)
                // pixel[3] = alpha — untouched
                dstRaster.setPixel(x, y, pixel)
            }
        }
        return out
    }
}