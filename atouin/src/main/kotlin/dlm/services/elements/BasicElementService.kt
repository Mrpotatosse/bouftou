package dlm.services.elements

import dlm.entities.Cell
import dlm.entities.elements.BasicElement
import dlm.entities.elements.ElementType
import extensions.readByte
import services.ParamsParserService
import java.nio.ByteBuffer

class BasicElementService(
    private val graphicalElementService: GraphicalElementService,
    private val soundElementService: SoundElementService
) : ParamsParserService<BasicElement, Cell> {
    override fun parse(raw: ByteBuffer, params: Cell): BasicElement {
        val type = ElementType.fromId(raw.readByte())
        return when (type) {
            ElementType.GRAPHICAL -> graphicalElementService.parse(raw, params)
            ElementType.SOUND -> soundElementService.parse(raw, params)
        }
    }
}