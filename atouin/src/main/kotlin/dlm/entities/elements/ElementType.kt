package dlm.entities.elements

enum class ElementType(val id: Byte) {
    GRAPHICAL(2),
    SOUND(33);

    companion object {
        private val BY_ID = ElementType.entries.associateBy(ElementType::id)

        fun fromId(id: Byte) = BY_ID[id]!!
    }
}