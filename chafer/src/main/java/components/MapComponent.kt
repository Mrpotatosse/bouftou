package components

import java.awt.BorderLayout
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.JPanel

class MapComponent : JPanel(BorderLayout()) {
    var mapImage: BufferedImage? = null

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val img = mapImage ?: return
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        val x = (width - img.width) / 2
        val y = (height - img.height) / 2
        g2.drawImage(img, x, y, img.width, img.height, null)
    }
}