package d2p.services

import d2p.entitites.D2PEntry
import stores.OffHeapReader
import stores.OffHeapStore
import java.io.File
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
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

            entryService.parse(buffer, Pair(path, root)).apply {
                buffer.clear()
            }
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

    fun parseEntryFromFolderAsStore(dir: Path): OffHeapReader<String, D2PEntry> {
        val result = OffHeapStore<String, D2PEntry>(
            File("cache/d2p.bin")
                .also { it.parentFile?.mkdirs(); it.delete() })

        Files.walk(dir).use { stream ->
            stream
                .filter { Files.isRegularFile(it) && it.extension == "d2p" }
                .sorted()
                .forEach { file ->
                    result.putAll(parseEntryFromFile(file, dir))
                }
        }

        return result.seal()
    }

    fun parseEntryFromFolderAsStore(path: String) =
        parseEntryFromFolderAsStore(Path.of(path))

    fun parseDataFromEntry(entry: D2PEntry) =
        FileChannel.open(Path.of(entry.path), StandardOpenOption.READ).use { channel ->
            val buffer = channel.map(
                FileChannel.MapMode.READ_ONLY,
                entry.offset.toLong(),
                entry.size.toLong()
            )

            dataService.parse(buffer, entry)
        }


}