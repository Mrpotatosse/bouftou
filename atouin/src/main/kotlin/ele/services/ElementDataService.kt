package ele.services

import ele.entities.ElementData
import ele.entities.ElementEntry
import ele.entities.GraphicalElementData
import ele.entities.GraphicalEntry
import extensions.readInt
import extensions.readUnsignedShort
import services.ParamsParserService
import java.nio.ByteBuffer

class ElementDataService(
    private val graphicalElementDataService: GraphicalElementDataService
) : ParamsParserService<Map<Int, Pair<ElementData, GraphicalElementData>>, ElementEntry> {
    override fun parse(raw: ByteBuffer, params: ElementEntry) =
        mutableMapOf<Int, Pair<ElementData, GraphicalElementData>>().let { result ->
            repeat(params.elementsCount.toInt()) {
                if (params.fileVersion >= 9) raw.readUnsignedShort()
                val elementId = raw.readInt()

                val elementData = ElementData(elementId, raw.position())
                val graphicalData = graphicalElementDataService.parse(raw, GraphicalEntry(params, elementData))
                result[elementId] = Pair(elementData, graphicalData)
            }
            result
        }
}