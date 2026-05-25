package ele.entities.subtypes

import ele.entities.GraphicalElementType

class BlendedGraphicalElementData(id: Int, type: GraphicalElementType) : NormalGraphicalElementData(id, type) {
    var blendMode: String = ""
}