package ele.services

import ele.entities.ElementEntry
import extensions.readInt
import services.ParamsParserService
import java.nio.ByteBuffer

class ElementIsJpgService : ParamsParserService<HashSet<Int>, ElementEntry> {
    override fun parse(raw: ByteBuffer, params: ElementEntry) =
        (if (params.fileVersion >= 8) HashSet.newHashSet<Int>(raw.readInt()).let { result ->
            repeat(result.size) {
                result.add(raw.readInt())
            }
            result
        } else HashSet.newHashSet(0))!!
    /*mutableSetOf<Int>().let { result ->
        if (params.fileVersion >= 8) {
            val gfxCount = raw.readInt()
            repeat(gfxCount) { result.add(raw.readInt()) }
        }
        result
    }*/
}