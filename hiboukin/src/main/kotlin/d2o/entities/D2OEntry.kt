package d2o.entities

data class D2OEntry(
    val indexes: Map<Int, Int> = mutableMapOf<Int, Int>(),
    val classes: Map<Int, D2OEntryClass> = mapOf(),
    val path: String = ""
)