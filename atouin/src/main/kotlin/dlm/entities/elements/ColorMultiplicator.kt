package dlm.entities.elements

data class ColorMultiplicator(
    val red: Byte,
    val green: Byte,
    val blue: Byte,
    val isAlreadyComputed: Boolean = false
) {
    companion object {
        fun clamp(value: Int, min: Double, max: Double): Double = value.toDouble().coerceIn(min, max)
    }
}