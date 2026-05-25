package ele.services

import ele.entities.GraphicalElementData
import ele.entities.GraphicalElementType
import ele.entities.GraphicalEntry
import ele.services.subtypes.*
import extensions.readByte
import services.ParamsParserService
import java.nio.ByteBuffer

class GraphicalElementDataService(
    private val animatedGraphicalElementDataService: AnimatedGraphicalElementDataService,
    private val blendedGraphicalElementDataService: BlendedGraphicalElementDataService,
    private val boundingBoxGraphicalElementDataService: BoundingBoxGraphicalElementDataService,
    private val entityGraphicalElementDataService: EntityGraphicalElementDataService,
    private val normalGraphicalElementDataService: NormalGraphicalElementDataService,
    private val particlesGraphicalElementDataService: ParticlesGraphicalElementDataService
) : ParamsParserService<GraphicalElementData, GraphicalEntry> {
    override fun parse(raw: ByteBuffer, params: GraphicalEntry): GraphicalElementData {
        val type = GraphicalElementType.fromId(raw.readByte())
        return when (type) {
            GraphicalElementType.BOUNDING_BOX -> boundingBoxGraphicalElementDataService.parse(raw, params)
            GraphicalElementType.ANIMATED -> animatedGraphicalElementDataService.parse(raw, params)
            GraphicalElementType.BLENDED -> blendedGraphicalElementDataService.parse(raw, params)
            GraphicalElementType.NORMAL -> normalGraphicalElementDataService.parse(raw, params)
            GraphicalElementType.ENTITY -> entityGraphicalElementDataService.parse(raw, params)
            GraphicalElementType.PARTICLES -> particlesGraphicalElementDataService.parse(raw, params)
        }
    }
}