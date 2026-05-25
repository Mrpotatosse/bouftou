package d2p.services

import d2p.entitites.D2PData
import d2p.entitites.D2PEntry
import extensions.readBytes
import extensions.seek
import services.ParamsParserService
import java.nio.ByteBuffer

class D2PDataService : ParamsParserService<D2PData, D2PEntry> {
    override fun parse(raw: ByteBuffer, params: D2PEntry) = raw.seek(params.offset).let {
        D2PData(params.key, raw.readBytes(params.size))
    }
}