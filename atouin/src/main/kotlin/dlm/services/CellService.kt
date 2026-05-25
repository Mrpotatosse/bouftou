package dlm.services

import dlm.entities.Cell
import dlm.services.elements.BasicElementService
import extensions.readShort
import services.ParamsParserService
import java.nio.ByteBuffer

class CellService(
    private val basicElementService: BasicElementService
) : ParamsParserService<Cell, Cell> {
    override fun parse(raw: ByteBuffer, params: Cell): Cell {
        params.cellId = raw.readShort()
        params.elementsCount = raw.readShort()

        repeat(params.elementsCount.toInt()) {
            params.elements.add(basicElementService.parse(raw, params))
        }
        return params
    }
}