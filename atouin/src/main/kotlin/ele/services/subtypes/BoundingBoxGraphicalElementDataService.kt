package ele.services.subtypes

import ele.entities.GraphicalElementType
import ele.entities.GraphicalEntry
import ele.entities.subtypes.BoundingBoxGraphicalElementData
import services.ParamsParserService
import java.nio.ByteBuffer

class BoundingBoxGraphicalElementDataService(
    private val normalGraphicalElementDataService: NormalGraphicalElementDataService
) : ParamsParserService<BoundingBoxGraphicalElementData, GraphicalEntry> {
    override fun parse(
        raw: ByteBuffer,
        params: GraphicalEntry
    ): BoundingBoxGraphicalElementData {
        val element = normalGraphicalElementDataService.parse(raw, params) as BoundingBoxGraphicalElementData
        element.type = GraphicalElementType.BOUNDING_BOX
        return element
    }
}