package ele.services.subtypes

import ele.entities.GraphicalElementType
import ele.entities.GraphicalEntry
import ele.entities.subtypes.BlendedGraphicalElementData
import extensions.readUTF
import services.ParamsParserService
import java.nio.ByteBuffer

class BlendedGraphicalElementDataService(
    private val normalGraphicalElementDataService: NormalGraphicalElementDataService
) : ParamsParserService<BlendedGraphicalElementData, GraphicalEntry> {
    override fun parse(
        raw: ByteBuffer,
        params: GraphicalEntry
    ): BlendedGraphicalElementData {
        val element = normalGraphicalElementDataService.parse(raw, params) as BlendedGraphicalElementData
        element.blendMode = raw.readUTF()
        element.type = GraphicalElementType.BLENDED
        return element
    }
}