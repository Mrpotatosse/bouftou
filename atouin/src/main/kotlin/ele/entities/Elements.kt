package ele.entities

data class Elements(
    val entry: ElementEntry,
    val data: Map<Int, Pair<ElementData, GraphicalElementData>>,
    val isJpg: HashSet<Int>
)