package dlm.entities

import ele.entities.Point

class Fixture(
    val map: Map
) {
    var fixtureId: Int = 0
    lateinit var offset: Point

    var redMultiplier: Byte = 0
    var greenMultiplier: Byte = 0
    var blueMultiplier: Byte = 0

    /** Bitwise-OR of the three colour multipliers (matches AS3 hue computation). */
    var hue: Byte = 0

    /** Alpha value (0–255). */
    var alpha: Byte = 0

    var xScale: Short = 0
    var yScale: Short = 0
    var rotation: Short = 0
}