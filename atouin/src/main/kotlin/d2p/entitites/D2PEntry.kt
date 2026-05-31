package d2p.entitites

import java.nio.file.Path

data class D2PEntry(
    val offset: Int,
    val size: Int,
    val key: String,
    val path: Path
)