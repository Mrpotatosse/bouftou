package dlm.entities

enum class MapType(val id: Byte) {
    OUTDOOR(0),
    INDOOR(1);

    companion object {
        private val BY_ID = MapType.entries.associateBy(MapType::id)

        fun fromId(id: Byte) = BY_ID[id]!!
    }
}