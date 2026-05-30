package d2o.services

import d2o.entities.D2OEntry
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.use

class D2OService(
    private val entryService: D2OEntryService
) {
    fun parseEntryFromFile(path: Path, root: Path? = null) =
        if (path.extension == "d2o") FileChannel.open(path, StandardOpenOption.READ).use { channel ->
            val buffer = channel.map(
                FileChannel.MapMode.READ_ONLY,
                0,
                channel.size()
            )

            entryService.parse(buffer, Pair(path, root))
        } else throw IllegalArgumentException("invalid file path extension")


    fun parseEntryFromFile(path: String) =
        parseEntryFromFile(Path.of(path))

    fun parseEntryFromFolder(dir: Path): Map<String, D2OEntry> {
        val result = LinkedHashMap<String, D2OEntry>()

        Files.walk(dir).use { stream ->
            stream
                .filter { Files.isRegularFile(it) && it.extension == "d2o" }
                .sorted()
                .forEach { file ->
                    result[file.name] = parseEntryFromFile(file, dir)
                }
        }

        return result
    }

    fun parseEntryFromFolder(path: String) =
        parseEntryFromFolder(Path.of(path))
}