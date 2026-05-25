package dlm.entities.elements

import dlm.entities.Cell

class SoundElement(cell: Cell) : BasicElement(cell, ElementType.SOUND) {
    var soundId: Int = 0
    var baseVolume: Short = 0
    var fullVolumeDistance: Int = 0
    var nullVolumeDistance: Int = 0
    var minDelayBetweenLoops: Short = 0
    var maxDelayBetweenLoops: Short = 0
}