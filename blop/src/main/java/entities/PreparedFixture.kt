package entities

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

data class PreparedFixture(
    val image: BufferedImage,
    val transform: AffineTransform,
    val alpha: Float
)