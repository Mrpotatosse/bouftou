package dlm.entities

class Layer(
    val dofusMap: DofusMap
) {
    var layerId: Int = 0
    var refCell: Int = 0
    var cellsCount: Short = 0
    val cells: MutableList<Cell> = mutableListOf()
}