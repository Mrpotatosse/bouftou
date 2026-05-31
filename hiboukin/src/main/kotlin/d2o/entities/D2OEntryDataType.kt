package d2o.entities

enum class D2OEntryDataType(val id: Int) {
    INT(-1),
    BOOLEAN(-2),
    STRING(-3),
    NUMBER(-4),
    I18N(-5),
    UINT(-6),
    VECTOR(-99);

    companion object {
        private val BY_ID = D2OEntryDataType.entries.associateBy(D2OEntryDataType::id)
        fun fromId(id: Int) = BY_ID[id]
    }
}