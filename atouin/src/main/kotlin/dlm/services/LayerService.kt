package dlm.services

import dlm.entities.Cell
import dlm.entities.Layer
import extensions.readByte
import extensions.readInt
import extensions.readShort
import services.ParamsParserService
import java.nio.ByteBuffer

class LayerService(
    private val cellService: CellService
) : ParamsParserService<Layer, Layer> {
    override fun parse(raw: ByteBuffer, params: Layer): Layer {
        params.layerId = if (params.dofusMap.mapVersion >= 9) raw.readByte().toInt() else raw.readInt()
        params.cellsCount = raw.readShort()
        repeat(params.cellsCount.toInt()) {
            params.cells.add(cellService.parse(raw, Cell(params)))
        }
        return params
    }
}