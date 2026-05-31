package d2o.entities

open class D2OEntryField(
    val name: String,
    val typeId: Int,
    val type: D2OEntryDataType?
) {
    override fun toString(): String {
        return "$name ($typeId) $type"
    }
}