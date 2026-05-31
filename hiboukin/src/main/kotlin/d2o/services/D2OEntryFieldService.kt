package d2o.services

import d2o.entities.D2OEntryField
import d2o.entities.D2OEntryVectorField
import d2o.entities.D2OEntryDataType
import extensions.readInt
import extensions.readUTF
import services.ParserService
import java.nio.ByteBuffer
import java.util.LinkedHashSet.newLinkedHashSet

class D2OEntryFieldService : ParserService<LinkedHashSet<D2OEntryField>> {
    override fun parse(raw: ByteBuffer): LinkedHashSet<D2OEntryField> {
        val len = raw.readInt()
        val fields = newLinkedHashSet<D2OEntryField>(len)
        (0 until len).forEach { _ ->
            fields.add(parseField(raw))
        }
        return fields
    }

    private fun parseField(raw: ByteBuffer): D2OEntryField {
        val name = raw.readUTF()
        val typeId = raw.readInt()
        return when (val type = D2OEntryDataType.fromId(typeId)) {
            D2OEntryDataType.VECTOR -> D2OEntryVectorField(parseField(raw), name, typeId, type)
            else -> D2OEntryField(name, typeId, type)
        }
    }
}