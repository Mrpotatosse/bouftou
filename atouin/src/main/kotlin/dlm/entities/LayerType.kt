package dlm.entities

enum class LayerType(val id: Byte) {
    LAYER_GROUND(0),
    LAYER_ADDITIONAL_GROUND(1),
    LAYER_DECOR(2),
    LAYER_ADDITIONAL_DECOR(3);

    companion object {
        private val BY_ID = entries.associateBy(LayerType::id)

        fun fromId(id: Byte) = BY_ID[id]!!
    }
}