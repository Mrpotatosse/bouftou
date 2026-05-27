package dlm.entities.elements

data class ColorMultiplicator(
    val red: Double,
    val green: Double,
    val blue: Double,
    val isAlreadyComputed: Boolean = false
) {
    companion object {
        fun clamp(value: Double, min: Double, max: Double): Double = value.coerceIn(min, max)
    }
}