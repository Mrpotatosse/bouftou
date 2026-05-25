package ele.entities.subtypes

import ele.entities.GraphicalElementData
import ele.entities.GraphicalElementType
import ele.entities.Point

open class NormalGraphicalElementData(id: Int, type: GraphicalElementType) : GraphicalElementData(id, type) {
    var gfxId: Int = 0
    var height: Byte = 0
    var horizontalSymmetry: Boolean = false

    lateinit var origin: Point
    lateinit var size: Point
}
