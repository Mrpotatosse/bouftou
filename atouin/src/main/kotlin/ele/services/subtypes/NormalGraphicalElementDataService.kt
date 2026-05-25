package ele.services.subtypes

import ele.entities.GraphicalElementType
import ele.entities.GraphicalEntry
import ele.entities.Point
import ele.entities.subtypes.NormalGraphicalElementData
import extensions.readBoolean
import extensions.readByte
import extensions.readInt
import extensions.readShort
import services.ParamsParserService
import java.nio.ByteBuffer

class NormalGraphicalElementDataService : ParamsParserService<NormalGraphicalElementData, GraphicalEntry> {
    override fun parse(
        raw: ByteBuffer,
        params: GraphicalEntry
    ) = typedParse(raw, params, ::NormalGraphicalElementData, GraphicalElementType.NORMAL)

    fun <T : NormalGraphicalElementData> typedParse(
        raw: ByteBuffer,
        params: GraphicalEntry,
        ctor: (id: Int, type: GraphicalElementType) -> T,
        type: GraphicalElementType
    ): T {
        val result = ctor(params.elementData.elementId, type)
        result.gfxId = raw.readInt()
        result.height = raw.readByte()
        result.horizontalSymmetry = raw.readBoolean()
        result.origin = Point(
            raw.readShort(),
            raw.readShort()
        )
        result.size = Point(
            raw.readShort(),
            raw.readShort()
        )
        return result
    }
}
