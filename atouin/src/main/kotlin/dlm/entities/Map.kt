package dlm.entities

data class Map(
    var mapVersion: Byte,
    val id: UInt,

    // ── Header / version ───────────────────────────────────────────────
    var encrypted: Boolean = false,
    var encryptionVersion: Byte = 0,

    // ── Identity ───────────────────────────────────────────────────────
    var relativeId: UInt = 0u, // AS3 uint → Long
    var mapType: Byte = 0,

    // ── Neighbour map ids ──────────────────────────────────────────────
    var topNeighbourId: Int = 0,
    var bottomNeighbourId: Int = 0,
    var leftNeighbourId: Int = 0,
    var rightNeighbourId: Int = 0,

    // ── Sub-area / audio ──────────────────────────────────────────────
    var subareaId: Int = 0,
    var shadowBonusOnEntities: UInt = 0u,
    var useLowPassFilter: Boolean = false,
    var useReverb: Boolean = false,
    var presetId: Int = -1,

    // ── Colours ────────────────────────────────────────────────────────
    var backgroundAlpha: Byte = 0,
    var backgroundRed: Byte = 0,
    var backgroundGreen: Byte = 0,
    var backgroundBlue: Byte = 0,

    /** Packed ARGB background colour derived from the individual channel fields. */
    var backgroundColor: Long = 0,

    /** Packed ARGB grid overlay colour. */
    var gridColor: Long = 0,

    // ── Zoom ───────────────────────────────────────────────────────────
    var zoomScale: Double = 1.0,
    var zoomOffsetX: Short = 0,
    var zoomOffsetY: Short = 0,

    // ── Ground cache ───────────────────────────────────────────────────
    var groundCRC: Int = 0,
    var groundCacheCurrentlyUsed: Int = 0,

    // ── Counts ────────────────────────────────────────────────────────
    var backgroundsCount: Byte = 0,
    var foregroundsCount: Byte = 0,
    var cellsCount: Int = 0,
    var layersCount: Byte = 0,

    // ── Collections ───────────────────────────────────────────────────
    val backgroundFixtures: MutableList<Fixture> = mutableListOf(),
    val foregroundFixtures: MutableList<Fixture> = mutableListOf(),
    val layers: MutableList<Layer> = mutableListOf(),
    val cells: MutableList<CellData> = mutableListOf(),

    // ── Arrow cells (cell ids that show a navigation arrow) ────────────
    val topArrowCell: MutableList<Int> = mutableListOf(),
    val leftArrowCell: MutableList<Int> = mutableListOf(),
    val bottomArrowCell: MutableList<Int> = mutableListOf(),
    val rightArrowCell: MutableList<Int> = mutableListOf(),

    // ── Movement system flag ───────────────────────────────────────────
    var isUsingNewMovementSystem: Boolean = false
)