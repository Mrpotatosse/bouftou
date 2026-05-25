package ele.entities.subtypes

import ele.entities.GraphicalElementData
import ele.entities.GraphicalElementType

class ParticlesGraphicalElementData(id: Int, type: GraphicalElementType) : GraphicalElementData(id, type) {
    var scriptId: Short = 0
}