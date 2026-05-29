package services

import extensions.fillRectRaw
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage

class GraphicService {
    fun createCanvas(canvasW: Int, canvasH: Int) =
        BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB)

    fun getGraphic(buffer: BufferedImage) = buffer.createGraphics().let {
        it.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        it.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        it.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        it!!
    }

    fun fillBackground(buffer: BufferedImage, colorRaw: Long) = createCanvas(buffer.width, buffer.height)
        .let { raw ->
            getGraphic(raw).let { graphic ->
                graphic.fillRectRaw(colorRaw, raw.width, raw.height)
                println(raw)
                graphic.drawImage(buffer, 0, 0, raw.width, raw.height, null)
                graphic.dispose()
            }
            raw
        }

    private fun applyColorTransform(
        buffer: BufferedImage,
        redMul: Float,
        greenMul: Float,
        blueMul: Float,
    ): BufferedImage {
        val w = buffer.width
        val h = buffer.height
        val out = createCanvas(w, h)
        val srcRaster = buffer.raster
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

    fun convertToArgb(src: BufferedImage): BufferedImage {
        val out = BufferedImage(src.width, src.height, BufferedImage.TYPE_INT_ARGB)
        val g = out.createGraphics()
        g.drawImage(src, 0, 0, null)
        g.dispose()
        return out
    }

    fun buildGfxImage(
        buffer: BufferedImage,
        flipHorizontally: Boolean,
        colorTransformRed: Float,
        colorTransformGreen: Float,
        colorTransformBlue: Float,
    ): BufferedImage {
        var img = if (buffer.type == BufferedImage.TYPE_INT_ARGB) buffer
        else convertToArgb(buffer)

        if (flipHorizontally) {
            val w = img.width
            val h = img.height
            val tx = AffineTransform.getScaleInstance(-1.0, 1.0)
            tx.translate(-w.toDouble(), 0.0)
            img = AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
                .filter(img, createCanvas(w, h))
        }

        val isIdentity =
            colorTransformRed == 1f && colorTransformGreen == 1f && colorTransformBlue == 1f
        if (!isIdentity) {
            img = applyColorTransform(
                img,
                colorTransformRed,
                colorTransformGreen,
                colorTransformBlue
            )
        }

        return img
    }
}