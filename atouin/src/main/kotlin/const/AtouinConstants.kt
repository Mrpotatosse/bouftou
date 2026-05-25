package const

import kotlin.math.pow

/**
 * Global constants for the Atouin map engine.
 * ActionScript origin: AtouinConstants.as
 */
object AtouinConstants {

    // ── Debug flags ────────────────────────────────────────────────────
    const val DEBUG_FILES_PARSING: Boolean = false
    const val DEBUG_FILES_PARSING_ELEMENTS: Boolean = false

    // ── Map grid dimensions ────────────────────────────────────────────
    const val MAP_WIDTH: Int = 14
    const val MAP_HEIGHT: Int = 20
    const val MAP_CELLS_COUNT: Int = 560

    // ── Adjacent-cell margins ──────────────────────────────────────────
    const val ADJACENT_CELL_LEFT_MARGIN: Int = 5
    const val ADJACENT_CELL_RIGHT_MARGIN: Int = 5

    // ── Cell pixel dimensions ──────────────────────────────────────────
    const val CELL_WIDTH: Int = 86
    const val CELL_HALF_WIDTH: Double = 43.0
    const val CELL_HEIGHT: Int = 43
    const val CELL_HALF_HEIGHT: Double = 21.5

    // ── Misc rendering ────────────────────────────────────────────────
    const val ALTITUDE_PIXEL_UNIT: Int = 10
    const val OVERLAY_MODE_ALPHA: Double = 0.7
    const val MAX_ZOOM: Int = 4
    const val MAX_GROUND_CACHE_MEMORY: Int = 5
    const val GROUND_MAP_VERSION: Int = 2
    const val PSEUDO_INFINITE: Int = 63

    // ── Resource-loader pool ───────────────────────────────────────────
    const val LOADERS_POOL_INITIAL_SIZE: Int = 30
    const val LOADERS_POOL_GROW_SIZE: Int = 5
    const val LOADERS_POOL_WARN_LIMIT: Int = 100

    // ── Disk / memory limits ───────────────────────────────────────────
    val MIN_DISK_SPACE_AVAILABLE: Double = 2.0.pow(20.0) * 512

    // ── Pathfinder bounds ─────────────────────────────────────────────
    const val PATHFINDER_MIN_X: Int = 0
    const val PATHFINDER_MAX_X: Int = 34   // 33 + 1
    const val PATHFINDER_MIN_Y: Int = -19
    const val PATHFINDER_MAX_Y: Int = 14   // 13 + 1

    // ── Wide-screen cell detection ────────────────────────────────────
    const val VIEW_DETECT_CELL_WIDTH: Int = 2 * CELL_WIDTH

    // ── Map coordinate bounds ─────────────────────────────────────────
    const val MIN_MAP_X: Int = -255
    const val MAX_MAP_X: Int = 255
    const val MIN_MAP_Y: Int = -255
    const val MAX_MAP_Y: Int = 255

    // ── Map decryption ────────────────────────────────────────────────
    const val MAP_KEY: String = "649ae451ca33ec53bbcbcc33becf15f4"

    // ── Movement types ────────────────────────────────────────────────
    const val MOVEMENT_WALK: Int = 1
    const val MOVEMENT_NORMAL: Int = 2

    // ── Mutable – set once display options are applied ────────────────
    @JvmField
    var WIDESCREEN_BITMAP_WIDTH: Int = 0
}
