package d2o.services

import d2o.entities.D2OEntry
import extensions.readInt
import extensions.readUTFBytes
import extensions.seek
import services.ParamsParserService
import java.nio.ByteBuffer
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class D2OEntryService(
    private val d2oEntryIndexService: D2OEntryIndexService,
    private val d2oEntryClassService: D2OEntryClassService,
) : ParamsParserService<D2OEntry, Pair<Path, Path?>> {
    override fun parse(raw: ByteBuffer, params: Pair<Path, Path?>): D2OEntry {
        val header = raw.readUTFBytes(3)
        if (header != "D2O") throw IllegalArgumentException("invalid buffer header")
        val offset = raw.readInt()
        val indexes = d2oEntryIndexService.parse(raw.seek(offset))
        val classes = d2oEntryClassService.parse(raw)
        return D2OEntry(indexes, classes, params.first.absolutePathString())
    }
}