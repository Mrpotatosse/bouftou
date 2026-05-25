package ele.services.subtypes

import ele.entities.GraphicalElementType
import ele.entities.GraphicalEntry
import ele.entities.subtypes.ParticlesGraphicalElementData
import extensions.readShort
import services.ParamsParserService
import java.nio.ByteBuffer

class ParticlesGraphicalElementDataService : ParamsParserService<ParticlesGraphicalElementData, GraphicalEntry> {
    override fun parse(
        raw: ByteBuffer,
        params: GraphicalEntry
    ): ParticlesGraphicalElementData {
        val result = ParticlesGraphicalElementData(params.elementData.elementId, GraphicalElementType.PARTICLES)
        result.scriptId = raw.readShort()
        return result
    }
}