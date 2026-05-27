package dlm.services.elements

import const.AtouinConstants
import dlm.entities.Cell
import dlm.entities.elements.ColorMultiplicator
import dlm.entities.elements.GraphicalElement
import extensions.readByte
import extensions.readShort
import extensions.readUnsignedInt
import services.ParamsParserService
import java.awt.geom.Point2D
import java.nio.ByteBuffer

class GraphicalElementService : ParamsParserService<GraphicalElement, Cell> {
    override fun parse(raw: ByteBuffer, params: Cell): GraphicalElement {
        val result = GraphicalElement(params)

        result.elementId = raw.readUnsignedInt()
        result.hue = ColorMultiplicator(
            raw.readByte().toDouble(),
            raw.readByte().toDouble(),
            raw.readByte().toDouble()
        )
        result.shadow = ColorMultiplicator(
            raw.readByte().toDouble(),
            raw.readByte().toDouble(),
            raw.readByte().toDouble()
        )
        if (params.layer.map.mapVersion <= 4) {
            result.offset = Point2D.Double(
                raw.readByte().toDouble(),
                raw.readByte().toDouble()
            )
            result.pixelOffset = Point2D.Double(
                result.offset.x * AtouinConstants.CELL_HALF_WIDTH,
                result.offset.y * AtouinConstants.CELL_HALF_HEIGHT
            )
        } else {
            result.pixelOffset = Point2D.Double(
                raw.readShort().toDouble(),
                raw.readShort().toDouble()
            )
            result.offset = Point2D.Double(
                result.pixelOffset.x / AtouinConstants.CELL_HALF_WIDTH,
                result.pixelOffset.y / AtouinConstants.CELL_HALF_HEIGHT
            )
        }
        result.altitude = raw.readByte()
        result.identifier = raw.readUnsignedInt()
        calculateFinalTeint(result)
        return result
    }

    private fun calculateFinalTeint(params: GraphicalElement) {
        val r = ColorMultiplicator.clamp((params.hue.red + params.shadow.red + 128) * 2, 0.0, 512.0)
        val g = ColorMultiplicator.clamp((params.hue.green + params.shadow.green + 128) * 2, 0.0, 512.0)
        val b = ColorMultiplicator.clamp((params.hue.blue + params.shadow.blue + 128) * 2, 0.0, 512.0)
        params.finalTeint = ColorMultiplicator(
            r, g, b,
            isAlreadyComputed = true
        )
    }
}