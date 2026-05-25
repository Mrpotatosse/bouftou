package dlm.entities

class CellData(
    val map: Map,
    val id: Int
) {
    /** Floor altitude in pixel units (raw byte × 10). */
    var floor: Int = 0

    var speed: Int = 0

    /** Raw byte encoding map-transition directions. */
    var mapChangeData: Byte = 0

    /** Movement zone identifier (present only for mapVersion > 5). */
    var moveZone: Byte = 0

    var mov: Boolean = false
    var los: Boolean = false
    var nonWalkableDuringFight: Boolean = false
    var red: Boolean = false
    var blue: Boolean = false
    var farmCell: Boolean = false
    var havenbagCell: Boolean = false
    var visible: Boolean = false
    var nonWalkableDuringRP: Boolean = false

    /** Packed arrow flags (bits 0-3 = top/bottom/right/left). */
    var arrowFlags: Int = 0

    val useTopArrow: Boolean get() = (arrowFlags and 0x1) != 0
    val useBottomArrow: Boolean get() = (arrowFlags and 0x2) != 0
    val useRightArrow: Boolean get() = (arrowFlags and 0x4) != 0
    val useLeftArrow: Boolean get() = (arrowFlags and 0x8) != 0
}