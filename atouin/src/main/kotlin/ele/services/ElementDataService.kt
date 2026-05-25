package ele.services

import ele.entities.ElementData
import ele.entities.ElementEntry
import ele.entities.GraphicalEntry
import extensions.readInt
import extensions.readUnsignedShort
import services.ParamsParserService
import java.nio.ByteBuffer

class ElementDataService(
    private val graphicalElementDataService: GraphicalElementDataService
) : ParamsParserService<Map<Int, ElementData>, ElementEntry> {
    override fun parse(raw: ByteBuffer, params: ElementEntry) = mutableMapOf<Int, ElementData>().let { result ->
        repeat(params.elementsCount.toInt()) {
            if (params.fileVersion >= 9) raw.readUnsignedShort()
            val elementId = raw.readInt()

            result[elementId] = ElementData(elementId, raw.position())
            graphicalElementDataService.parse(raw, GraphicalEntry(params, result[elementId]!!))
        }
        result
    }
}