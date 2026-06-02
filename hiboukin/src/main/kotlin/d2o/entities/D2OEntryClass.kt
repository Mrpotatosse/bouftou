package d2o.entities

import java.util.LinkedHashSet.newLinkedHashSet

data class D2OEntryClass(
    val id: Int = 0,
    val memberName: String = "",
    val packageName: String = "",
    val fields: LinkedHashSet<D2OEntryField> = newLinkedHashSet(0),
)