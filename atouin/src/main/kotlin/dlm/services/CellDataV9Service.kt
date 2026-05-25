package dlm.services

import dlm.entities.CellData
import extensions.readUnsignedShort
import services.ParamsParserService
import java.nio.ByteBuffer

class CellDataV9Service : ParamsParserService<CellData, CellData> {
    override fun parse(raw: ByteBuffer, params: CellData): CellData {
        val tmp = raw.readUnsignedShort().toInt()

        params.mov = (tmp and 0x01) == 0
        params.nonWalkableDuringFight = (tmp and 0x02) != 0
        params.nonWalkableDuringRP = (tmp and 0x04) != 0
        params.los = (tmp and 0x08) == 0
        params.blue = (tmp and 0x10) != 0
        params.red = (tmp and 0x20) != 0
        params.visible = (tmp and 0x40) != 0
        params.farmCell = (tmp and 0x80) != 0

        val topArrow: Boolean
        val bottomArrow: Boolean
        val rightArrow: Boolean
        val leftArrow: Boolean

        if (params.map.mapVersion >= 10) {
            params.havenbagCell = (tmp and 0x0100) != 0
            topArrow = (tmp and 0x0200) != 0
            bottomArrow = (tmp and 0x0400) != 0
            rightArrow = (tmp and 0x0800) != 0
            leftArrow = (tmp and 0x1000) != 0
        } else {
            topArrow = (tmp and 0x0100) != 0
            bottomArrow = (tmp and 0x0200) != 0
            rightArrow = (tmp and 0x0400) != 0
            leftArrow = (tmp and 0x0800) != 0
        }

        if (topArrow) params.map.topArrowCell.add(params.id)
        if (bottomArrow) params.map.bottomArrowCell.add(params.id)
        if (rightArrow) params.map.rightArrowCell.add(params.id)
        if (leftArrow) params.map.leftArrowCell.add(params.id)
        return params
    }
}