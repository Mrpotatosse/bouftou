package ele.entities

data class Elements(
    val entry: ElementEntry,
    val data: Map<Int, ElementData>,
    val isJpg: Set<Int>
)