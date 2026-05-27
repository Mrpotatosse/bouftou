package render.entities

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

data class CanvasDraw(
    val img: BufferedImage,
    val x: Int,
    val y: Int,
    // AffineTransform applied before blitting (scale + rotation for fixtures,
    // identity for regular elements).  null = no transform needed.
    val transform: AffineTransform? = null,
    val flipH: Boolean = false,
    val redMul: Float = 1f,
    val greenMul: Float = 1f,
    val blueMul: Float = 1f,
    val alphaMul: Float = 1f,
)