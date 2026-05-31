package d2o.services

import extensions.readInt
import services.ParserService
import java.nio.ByteBuffer

class D2OEntryIndexService : ParserService<Map<Int, Int>> {
    override fun parse(raw: ByteBuffer): Map<Int, Int> {
        val len = raw.readInt()
        val indexes = mutableMapOf<Int, Int>()
        (0 until len / 8).forEach { _ ->
            indexes[raw.readInt()] = raw.readInt()
        }
        return indexes
    }
}