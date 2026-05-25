package ele.entities.subtypes

import ele.entities.GraphicalElementType

class AnimatedGraphicalElementData(id: Int, type: GraphicalElementType) : NormalGraphicalElementData(id, type) {
    var minDelay: Int = 0
    var maxDelay: Int = 0
}