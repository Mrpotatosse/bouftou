package ele.services

import ele.entities.ElementEntry
import extensions.readInt
import services.ParamsParserService
import java.nio.ByteBuffer

class ElementIsJpgService : ParamsParserService<Set<Int>, ElementEntry> {
    override fun parse(raw: ByteBuffer, params: ElementEntry) = mutableSetOf<Int>().let { result ->
        if (params.fileVersion >= 8u) {
            val gfxCount = raw.readInt()
            repeat(gfxCount) { result.add(raw.readInt()) }
        }
        result
    }
}