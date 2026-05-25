package ele.services.subtypes

import ele.entities.GraphicalElementType
import ele.entities.GraphicalEntry
import ele.entities.subtypes.EntityGraphicalElementData
import extensions.readBoolean
import extensions.readInt
import extensions.readUTF
import services.ParamsParserService
import java.nio.ByteBuffer

class EntityGraphicalElementDataService : ParamsParserService<EntityGraphicalElementData, GraphicalEntry> {
    override fun parse(
        raw: ByteBuffer,
        params: GraphicalEntry
    ): EntityGraphicalElementData {
        val result = EntityGraphicalElementData(params.elementData.elementId, GraphicalElementType.ENTITY)
        result.entityLook = raw.readUTF()
        result.horizontalSymmetry = raw.readBoolean()
        if (params.elementEntry.fileVersion >= 7u) result.playAnimation = raw.readBoolean()
        if (params.elementEntry.fileVersion >= 6u) result.playAnimStatic = raw.readBoolean()
        if (params.elementEntry.fileVersion >= 5u) {
            result.minDelay = raw.readInt()
            result.maxDelay = raw.readInt()
        }
        return result
    }
}