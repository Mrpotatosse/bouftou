package dlm.services

import dlm.entities.CellData
import extensions.readByte
import services.ParamsParserService
import java.nio.ByteBuffer

class CellDataService(
    private val cellDataV9Service: CellDataV9Service,
    private val cellDataLegacyService: CellDataLegacyService
) : ParamsParserService<CellData, CellData> {
    override fun parse(raw: ByteBuffer, params: CellData): CellData {
        params.floor = raw.readByte() * 10
        // Sentinel: -128 × 10 = -1280 → cell record is empty
        if (params.floor == -1280) return params
        if (params.dofusMap.mapVersion >= 9) {
            cellDataV9Service.parse(raw, params)
        } else {
            cellDataLegacyService.parse(raw, params)
        }

        params.speed = raw.readByte().toInt()
        params.mapChangeData = raw.readByte()

        if (params.dofusMap.mapVersion > 5) {
            params.moveZone = raw.readByte()
        }

        // Version 8 stores arrow flags in a separate byte (v9+ packs them in the flags short)
        if (params.dofusMap.mapVersion in 8..8) {
            val bits = raw.readByte().toInt()
            params.arrowFlags = bits and 0x0F
            if (params.useTopArrow) params.dofusMap.topArrowCell.add(params.id)
            if (params.useBottomArrow) params.dofusMap.bottomArrowCell.add(params.id)
            if (params.useLeftArrow) params.dofusMap.leftArrowCell.add(params.id)
            if (params.useRightArrow) params.dofusMap.rightArrowCell.add(params.id)
        }
        return params
    }
}