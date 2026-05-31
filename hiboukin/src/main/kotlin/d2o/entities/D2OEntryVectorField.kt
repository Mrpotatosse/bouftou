package d2o.entities

class D2OEntryVectorField(val fieldType: D2OEntryField, name: String, typeId: Int, type: D2OEntryDataType?) :
    D2OEntryField(name, typeId, type)