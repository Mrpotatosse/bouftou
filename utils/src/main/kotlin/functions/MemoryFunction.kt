package functions

fun memoryStats(name: String): String {
    val rt = Runtime.getRuntime()
    val used = (rt.totalMemory() - rt.freeMemory()) / 1_048_576L
    val total = rt.totalMemory() / 1_048_576L
    val max = rt.maxMemory() / 1_048_576L
    return "[$name] mem: ${used}MB used / ${total}MB committed / ${max}MB max"
}