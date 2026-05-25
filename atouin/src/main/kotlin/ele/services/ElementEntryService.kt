package ele.services

import ele.entities.ElementEntry
import extensions.readUnsignedByte
import extensions.readUnsignedInt
import services.ParamsParserService
import java.nio.ByteBuffer

class ElementEntryService : ParamsParserService<ElementEntry, String> {
    override fun parse(raw: ByteBuffer, params: String) =
        raw.readUnsignedByte().let {
            if (it.toInt() != 69) throw IllegalArgumentException("Invalid Ele Entry")
            ElementEntry(
                raw.readUnsignedByte(),
                raw.readUnsignedInt(),
            )
        }
}