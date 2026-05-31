package d2o.services

import d2o.entities.D2OEntry
import d2o.entities.D2OObject
import d2o.entities.D2OObjectEntry
import extensions.readInt
import services.ParamsParserService
import java.nio.ByteBuffer

class D2OObjectService(
    private val d2oObjectFieldsService: D2OObjectFieldsService,
) : ParamsParserService<D2OObject, D2OEntry> {
    override fun parse(raw: ByteBuffer, params: D2OEntry): D2OObject {
        val typeId = raw.readInt()
        val d2oClass = params.classes[typeId] ?: throw IllegalArgumentException("Unknown type $typeId")
        val d2oFields = d2oObjectFieldsService.parse(raw, D2OObjectEntry(params, d2oClass))
        return D2OObject(d2oClass, d2oFields)
    }
}