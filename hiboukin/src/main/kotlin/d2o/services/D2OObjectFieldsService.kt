package d2o.services

import d2o.entities.*
import extensions.*
import services.ParamsParserService
import java.nio.ByteBuffer

class D2OObjectFieldsService : ParamsParserService<D2OObjectFields, D2OObjectEntry> {
    override fun parse(raw: ByteBuffer, params: D2OObjectEntry): D2OObjectFields {
        val result = D2OObjectFields()
        params.entryClass.fields.forEach { field ->
            result[field.name] = parseFieldValue(raw, params, field)
        }
        return result
    }

    private fun parseFieldValue(raw: ByteBuffer, params: D2OObjectEntry, field: D2OEntryField): Any? {
        return when (field.type) {
            D2OEntryDataType.INT -> raw.readInt()
            D2OEntryDataType.BOOLEAN -> raw.readBoolean()
            D2OEntryDataType.STRING -> raw.readUTF()
            D2OEntryDataType.NUMBER -> raw.readDouble()
            D2OEntryDataType.I18N -> raw.readInt()
            D2OEntryDataType.UINT -> raw.readUnsignedInt()
            D2OEntryDataType.VECTOR -> {
                require(field is D2OEntryVectorField) { "Expected Vector Entry for field '${field.name}'" }
                val len = raw.readInt()
                val result = mutableListOf<Any?>()
                (0 until len).forEach { _ ->
                    result.add(parseFieldValue(raw, params, field.fieldType!!))
                }
                result
            }

            else -> {
                val id = raw.readInt()
                if (id == -1431655766) {
                    null
                } else {
                    val entryClass = params.entry.classes[id]!!
                    parse(raw, D2OObjectEntry(params.entry, entryClass))
                }
            }
        }
    }
}