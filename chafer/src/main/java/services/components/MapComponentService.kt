package services.components

import components.MapComponent
import java.awt.image.BufferedImage

class MapComponentService {
    fun component(img: BufferedImage): MapComponent {
        val panel = MapComponent()
        panel.mapImage = img

        panel.setBounds(
            0,
            0,
            img.width,
            img.height
        )
        return panel
    }
}