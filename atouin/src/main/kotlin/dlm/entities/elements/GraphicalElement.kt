package dlm.entities.elements

import dlm.entities.Cell
import java.awt.geom.Point2D

class GraphicalElement(cell: Cell) : BasicElement(cell, ElementType.GRAPHICAL) {
    var elementId: UInt = 0u       // AS3 readUnsignedInt → Long
    lateinit var hue: ColorMultiplicator
    lateinit var shadow: ColorMultiplicator
    lateinit var finalTeint: ColorMultiplicator
    lateinit var offset: Point2D.Double
    lateinit var pixelOffset: Point2D.Double

    var altitude: Byte = 0
    var identifier: UInt = 0u       // AS3 readUnsignedInt → Long
    val colorMultiplicator: ColorMultiplicator get() = finalTeint
}