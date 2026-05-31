package d2o.entities

import java.nio.file.Path

data class D2OEntry(
    val indexes: Map<Int, Int>,
    val classes: Map<Int, D2OEntryClass>,
    val path: Path
)