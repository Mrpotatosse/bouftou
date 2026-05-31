package d2o.entities

class D2OObjectFields : LinkedHashMap<String, Any?>() {
    fun getAsInt(name: String) =
        this[name] as? Int ?: throw IllegalArgumentException("$name is not an instance of Int")

    fun getAsString(name: String) =
        this[name] as? String ?: throw IllegalArgumentException("$name is not an instance of String")

    fun getAsBoolean(name: String) =
        this[name] as? Boolean ?: throw IllegalArgumentException("$name is not an instance of Boolean")

    fun getAsNumber(name: String) =
        this[name] as? Double ?: throw IllegalArgumentException("$name is not an instance of Number")

    fun getAsI18N(name: String) =
        this[name] as? Int ?: throw IllegalArgumentException("$name is not an instance of I18N")

    fun getAsUint(name: String) =
        this[name] as? UInt ?: throw IllegalArgumentException("$name is not an instance of Uint")

    fun getAsVector(name: String) =
        this[name] as? MutableList<*> ?: throw IllegalArgumentException("$name is not an instance of MutableList<*>")

    fun getAsObject(name: String) =
        this[name] as? D2OObjectFields ?: throw IllegalArgumentException("$name is not an instance of D2OObjectFields")

    fun forEachAsIntVector(name: String, action: (Int) -> Unit) = getAsVector(name).forEach {
        action.invoke(it as? Int ?: throw IllegalArgumentException("$it is not an instance of Int"))
    }

    fun <R> mapAsIntVector(name: String, action: (Int) -> R) = getAsVector(name).map {
        action.invoke(it as? Int ?: throw IllegalArgumentException("$it is not an instance of Int"))
    }

    fun forEachAsStringVector(name: String, action: (String) -> Unit) = getAsVector(name).forEach {
        action.invoke(it as? String ?: throw IllegalArgumentException("$it is not an instance of String"))
    }

    fun <R> mapAsStringVector(name: String, action: (String) -> R) = getAsVector(name).map {
        action.invoke(it as? String ?: throw IllegalArgumentException("$it is not an instance of String"))
    }

    fun forEachAsBooleanVector(name: String, action: (Boolean) -> Unit) = getAsVector(name).forEach {
        action.invoke(it as? Boolean ?: throw IllegalArgumentException("$it is not an instance of Boolean"))
    }

    fun <R> mapAsBooleanVector(name: String, action: (Boolean) -> R) = getAsVector(name).map {
        action.invoke(it as? Boolean ?: throw IllegalArgumentException("$it is not an instance of Boolean"))
    }

    fun forEachAsNumberVector(name: String, action: (Double) -> Unit) = getAsVector(name).forEach {
        action.invoke(it as? Double ?: throw IllegalArgumentException("$it is not an instance of Double"))
    }

    fun <R> mapAsNumberVector(name: String, action: (Double) -> R) = getAsVector(name).map {
        action.invoke(it as? Double ?: throw IllegalArgumentException("$it is not an instance of Double"))
    }

    fun forEachAsI18NVector(name: String, action: (Int) -> Unit) = getAsVector(name).forEach {
        action.invoke(it as? Int ?: throw IllegalArgumentException("$it is not an instance of Int"))
    }

    fun <R> mapAsI18NVector(name: String, action: (Int) -> R) = getAsVector(name).map {
        action.invoke(it as? Int ?: throw IllegalArgumentException("$it is not an instance of Int"))
    }

    fun forEachAsUintVector(name: String, action: (UInt) -> Unit) = getAsVector(name).forEach {
        action.invoke(it as? UInt ?: throw IllegalArgumentException("$it is not an instance of UInt"))
    }

    fun <R> mapAsUintVector(name: String, action: (UInt) -> R) = getAsVector(name).map {
        action.invoke(it as? UInt ?: throw IllegalArgumentException("$it is not an instance of UInt"))
    }

    fun forEachAsObjectVector(name: String, action: (D2OObjectFields) -> Unit) = getAsVector(name).forEach {
        action.invoke(
            it as? D2OObjectFields ?: throw IllegalArgumentException("$it is not an instance of D2OObjectFields")
        )
    }

    fun <R> mapAsObjectVector(name: String, action: (D2OObjectFields) -> R) = getAsVector(name).map {
        action.invoke(
            it as? D2OObjectFields ?: throw IllegalArgumentException("$it is not an instance of D2OObjectFields")
        )
    }
}