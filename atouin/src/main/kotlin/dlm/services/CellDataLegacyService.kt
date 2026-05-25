package dlm.services

import dlm.entities.CellData
import extensions.readByte
import services.ParamsParserService
import java.nio.ByteBuffer

class CellDataLegacyService : ParamsParserService<CellData, CellData> {
    override fun parse(raw: ByteBuffer, params: CellData): CellData {
        val losmov = raw.readByte().toInt()

        params.mov = (losmov and 0x01) == 1
        params.los = ((losmov shr 1) and 0x01) == 1
        params.nonWalkableDuringFight = ((losmov shr 2) and 0x01) == 1
        params.red = ((losmov shr 3) and 0x01) == 1
        params.blue = ((losmov shr 4) and 0x01) == 1
        params.farmCell = ((losmov shr 5) and 0x01) == 1
        params.visible = ((losmov shr 6) and 0x01) == 1
        params.nonWalkableDuringRP = ((losmov shr 7) and 0x01) == 1

        return params
    }
}