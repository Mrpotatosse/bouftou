package ele.services.subtypes

import ele.entities.GraphicalElementType
import ele.entities.GraphicalEntry
import ele.entities.subtypes.BlendedGraphicalElementData
import extensions.readInt
import extensions.readUTFBytes
import services.ParamsParserService
import java.nio.ByteBuffer

class BlendedGraphicalElementDataService(
    private val normalGraphicalElementDataService: NormalGraphicalElementDataService
) : ParamsParserService<BlendedGraphicalElementData, GraphicalEntry> {
    override fun parse(
        raw: ByteBuffer,
        params: GraphicalEntry
    ): BlendedGraphicalElementData {
        val element = normalGraphicalElementDataService.typedParse(
            raw, params, ::BlendedGraphicalElementData,
            GraphicalElementType.BLENDED
        )
        element.blendMode = raw.readUTFBytes(raw.readInt())
        return element
    }
}