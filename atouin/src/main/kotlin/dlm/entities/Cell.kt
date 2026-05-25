package dlm.entities

import dlm.entities.elements.BasicElement

class Cell(
    val layer: Layer
) {
    var cellId: Short = 0
    var elementsCount: Short = 0
    val elements: MutableList<BasicElement> = mutableListOf()
}