package d2o.services

import d2o.entities.D2OEntryClass
import extensions.readInt
import extensions.readUTF
import services.ParserService
import java.nio.ByteBuffer

class D2OEntryClassService(
    private val d2oEntryFieldService: D2OEntryFieldService
) : ParserService<Map<Int, D2OEntryClass>> {
    override fun parse(raw: ByteBuffer): Map<Int, D2OEntryClass> {
        val len = raw.readInt()
        val classes = mutableMapOf<Int, D2OEntryClass>()
        (0 until len).forEach { i ->
            val id = raw.readInt()
            val memberName = raw.readUTF()
            val packageName = raw.readUTF()
            val fields = d2oEntryFieldService.parse(raw)
            classes[id] = D2OEntryClass(
                id,
                memberName,
                packageName,
                fields
            )
        }
        return classes
    }
}