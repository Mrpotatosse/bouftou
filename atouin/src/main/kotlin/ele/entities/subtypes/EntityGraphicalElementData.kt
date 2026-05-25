package ele.entities.subtypes

import ele.entities.GraphicalElementData
import ele.entities.GraphicalElementType

class EntityGraphicalElementData(id: Int, type: GraphicalElementType) : GraphicalElementData(id, type) {
    var entityLook: String = ""
    var horizontalSymmetry: Boolean = false
    var playAnimation: Boolean = false
    var playAnimStatic: Boolean = false
    var minDelay: Int = 0
    var maxDelay: Int = 0
}