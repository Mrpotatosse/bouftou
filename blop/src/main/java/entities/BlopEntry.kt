package entities

import d2p.entitites.D2PEntry
import dlm.entities.DofusMap
import ele.entities.Elements
import java.awt.image.BufferedImage

data class BlopEntry(
    val d2pEntry: Map<String, D2PEntry>,
    val elements: Elements,
    val dofusMap: DofusMap,
    val pngLoader: (id: Int) -> BufferedImage?,
    val jpgLoader: (id: Int) -> BufferedImage?
)