package services

import extensions.fillRectRaw
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage


class GraphicService {
    fun createCanvas(canvasW: Int, canvasH: Int) =
        BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB)

    fun getGraphic(buffer: BufferedImage): Graphics2D = buffer.createGraphics().apply {
        // ✅ SPEED hints — QUALITY/ANTIALIAS/BILINEAR are wasted on 1:1 drawImage calls
        setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED)
        setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED)
        setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED)
        setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE)
    }

    fun fillBackground(buffer: BufferedImage, colorRaw: Long): BufferedImage {
        val raw = createCanvas(buffer.width, buffer.height)
        val graphic = getGraphic(raw)
        graphic.fillRectRaw(colorRaw, raw.width, raw.height)
        graphic.drawImage(buffer, 0, 0, null)
        graphic.dispose()
        return raw
    }

    fun convertToArgb(src: BufferedImage): BufferedImage {
        val out = BufferedImage(src.width, src.height, BufferedImage.TYPE_INT_ARGB)
        val g = out.createGraphics()
        g.drawImage(src, 0, 0, null)
        g.dispose()
        return out
    }

    private fun applyColorTransform(
        buffer: BufferedImage,
        redMul: Double,
        greenMul: Double,
        blueMul: Double,
    ): BufferedImage {
        val w = buffer.width
        val h = buffer.height
        val out = createCanvas(w, h)

        // ✅ Single bulk call instead of w*h individual JNI calls
        val pixels = buffer.getRGB(0, 0, w, h, null, 0, w)

        for (i in pixels.indices) {
            val argb = pixels[i]
            val a = argb and 0xFF000000.toInt()
            // ✅ Bit-shift unpacking — no IntArray allocation, no getPixel overhead
            val r = ((argb shr 16 and 0xFF) * redMul).toInt().coerceIn(0, 255)
            val g = ((argb shr 8 and 0xFF) * greenMul).toInt().coerceIn(0, 255)
            val b = ((argb and 0xFF) * blueMul).toInt().coerceIn(0, 255)
            pixels[i] = a or (r shl 16) or (g shl 8) or b
        }

        // ✅ Single bulk write
        out.setRGB(0, 0, w, h, pixels, 0, w)
        return out
    }

    private fun flipHorizontal(src: BufferedImage): BufferedImage {
        val w = src.width
        val h = src.height
        val pixels = src.getRGB(0, 0, w, h, null, 0, w)
        val flipped = IntArray(pixels.size)

        // ✅ In-place mirror via index math — no AffineTransformOp, no extra canvas
        for (y in 0 until h) {
            val rowStart = y * w
            for (x in 0 until w) {
                flipped[rowStart + (w - 1 - x)] = pixels[rowStart + x]
            }
        }

        val out = createCanvas(w, h)
        out.setRGB(0, 0, w, h, flipped, 0, w)
        return out
    }

    fun buildGfxImage(
        buffer: BufferedImage,
        flipHorizontally: Boolean,
        colorTransformRed: Double,
        colorTransformGreen: Double,
        colorTransformBlue: Double,
    ): BufferedImage {
        var img = if (buffer.type == BufferedImage.TYPE_INT_ARGB) buffer
        else convertToArgb(buffer)

        if (flipHorizontally) {
            img = flipHorizontal(img)
        }

        val isIdentity =
            colorTransformRed == 1.0 && colorTransformGreen == 1.0 && colorTransformBlue == 1.0
        if (!isIdentity) {
            img = applyColorTransform(img, colorTransformRed, colorTransformGreen, colorTransformBlue)
        }

        return img
    }
}