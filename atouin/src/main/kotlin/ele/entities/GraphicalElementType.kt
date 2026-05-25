package ele.entities

enum class GraphicalElementType(val id: UByte) {
    NORMAL(0u),
    BOUNDING_BOX(1u),
    ANIMATED(2u),
    ENTITY(3u),
    PARTICLES(4u),
    BLENDED(5u);

    companion object {
        private val BY_ID = entries.associateBy(GraphicalElementType::id)

        fun fromId(id: UByte) = BY_ID[id]!!
    }
}