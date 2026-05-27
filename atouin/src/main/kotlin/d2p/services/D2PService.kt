package d2p.services

import d2p.entitites.D2PEntry
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.absolutePathString
import kotlin.io.path.extension

class D2PService(
    private val dataService: D2PDataService,
    private val entryService: D2PEntryService,
) {
    fun parseEntryFromFile(path: Path, root: Path? = null) =
        if (path.extension == "d2p") FileChannel.open(path, StandardOpenOption.READ).use { channel ->
            val buffer = channel.map(
                FileChannel.MapMode.READ_ONLY,
                0,
                channel.size()
            )

            val parent = root?.absolutePathString() ?: path.parent.absolutePathString()
            entryService.parse(buffer, Pair(path.absolutePathString(), parent))
        } else throw IllegalArgumentException("invalid file path extension")


    fun parseEntryFromFile(path: String) =
        parseEntryFromFile(Path.of(path))

    fun parseEntryFromFolder(dir: Path): Map<String, D2PEntry> {
        val result = LinkedHashMap<String, D2PEntry>()

        Files.walk(dir).use { stream ->
            stream
                .filter { Files.isRegularFile(it) && it.extension == "d2p" }
                .sorted()
                .forEach { file ->
                    result.putAll(parseEntryFromFile(file, dir))
                }
        }

        return result
    }

    fun parseEntryFromFolder(path: String) =
        parseEntryFromFolder(Path.of(path))

    fun parseDataFromEntry(entry: D2PEntry) =
        FileChannel.open(Path.of(entry.path), StandardOpenOption.READ).use { channel ->
            val buffer = channel.map(
                FileChannel.MapMode.READ_ONLY,
                0,
                channel.size()
            )

            dataService.parse(buffer, entry)
        }
}