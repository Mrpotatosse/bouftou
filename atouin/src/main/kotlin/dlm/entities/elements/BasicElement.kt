package dlm.entities.elements

import dlm.entities.Cell

abstract class BasicElement(
    val cell: Cell,
    val type: ElementType
)