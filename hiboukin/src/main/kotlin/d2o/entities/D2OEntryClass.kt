package d2o.entities

data class D2OEntryClass(
    val id: Int,
    val memberName: String,
    val packageName: String,
    val fields: LinkedHashSet<D2OEntryField>
)