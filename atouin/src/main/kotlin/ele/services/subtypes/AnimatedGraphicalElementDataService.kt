package ele.services.subtypes

import ele.entities.GraphicalElementType
import ele.entities.GraphicalEntry
import ele.entities.subtypes.AnimatedGraphicalElementData
import extensions.readInt
import services.ParamsParserService
import java.nio.ByteBuffer

class AnimatedGraphicalElementDataService(
    private val normalGraphicalElementDataService: NormalGraphicalElementDataService
) : ParamsParserService<AnimatedGraphicalElementData, GraphicalEntry> {
    override fun parse(
        raw: ByteBuffer,
        params: GraphicalEntry
    ): AnimatedGraphicalElementData {
        val element = normalGraphicalElementDataService.parse(raw, params) as AnimatedGraphicalElementData
        if (params.elementEntry.fileVersion in 4u..4u) {
            element.minDelay = raw.readInt()
            element.maxDelay = raw.readInt()
        }
        element.type = GraphicalElementType.ANIMATED
        return element
    }
}