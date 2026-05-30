package d2o.services

import d2o.entities.D2OEntry
import extensions.readUTFBytes
import services.ParamsParserService
import java.nio.ByteBuffer
import java.nio.file.Path

class D2OEntryService : ParamsParserService<D2OEntry, Pair<Path, Path?>> {
    override fun parse(raw: ByteBuffer, params: Pair<Path, Path?>): D2OEntry {
        val header = raw.readUTFBytes(3)
        if (header != "D2O") throw IllegalArgumentException("invalid buffer header")
        return D2OEntry()
    }
}