package ele.services

import ele.entities.Elements
import extensions.deflate
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.extension

class ElementsService(
    private val entryService: ElementEntryService,
    private val dataService: ElementDataService,
    private val isJpgService: ElementIsJpgService
) {
    fun parseElementsFromFile(path: Path) =
        if (path.extension == "ele") FileChannel.open(path, StandardOpenOption.READ).use { channel ->
            val buffer = channel.map(
                FileChannel.MapMode.READ_ONLY,
                0,
                channel.size()
            ).deflate()

            val entry = entryService.parse(buffer, path.toString())
            val data = dataService.parse(buffer, entry)
            val isJpg = isJpgService.parse(buffer, entry)
            Elements(entry, data, isJpg)
        } else throw IllegalArgumentException("invalid file path extension")

    fun parseElementsFromFile(path: String) =
        parseElementsFromFile(Path.of(path))
}