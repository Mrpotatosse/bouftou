package d2p.entitites

data class D2PData(
    val name: String,
    val buffer: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as D2PData

        if (name != other.name) return false
        if (!buffer.contentEquals(other.buffer)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + buffer.contentHashCode()
        return result
    }
}