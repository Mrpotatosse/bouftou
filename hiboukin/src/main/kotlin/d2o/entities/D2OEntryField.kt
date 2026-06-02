package d2o.entities

open class D2OEntryField(
    val name: String = "",
    val typeId: Int = 0,
    val type: D2OEntryDataType? = null
) {
    override fun toString(): String {
        return "$name ($typeId) $type"
    }
}