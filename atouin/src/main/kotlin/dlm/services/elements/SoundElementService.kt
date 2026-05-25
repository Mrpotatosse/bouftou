package dlm.services.elements

import dlm.entities.Cell
import dlm.entities.elements.SoundElement
import extensions.readInt
import extensions.readShort
import services.ParamsParserService
import java.nio.ByteBuffer

class SoundElementService : ParamsParserService<SoundElement, Cell> {
    override fun parse(raw: ByteBuffer, params: Cell): SoundElement {
        val result = SoundElement(params)
        result.soundId = raw.readInt()
        result.baseVolume = raw.readShort()
        result.fullVolumeDistance = raw.readInt()
        result.nullVolumeDistance = raw.readInt()
        result.minDelayBetweenLoops = raw.readShort()
        result.maxDelayBetweenLoops = raw.readShort()
        return result
    }
}