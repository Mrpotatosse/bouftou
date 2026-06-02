package extensions

inline fun <T> T.profile(
    label: String,
    block: (T) -> T
): T {
    val start = System.nanoTime()
    val result = block(this)

    println(
        "│ %-18s │ %9.2f ms │"
            .format(label, (System.nanoTime() - start) / 1_000_000.0)
    )

    return result
}