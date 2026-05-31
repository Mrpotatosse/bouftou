package d2p.services

import d2p.entitites.D2PData
import d2p.entitites.D2PEntry
import extensions.readBytes
import services.ParamsParserService
import java.nio.ByteBuffer

class D2PDataService : ParamsParserService<D2PData, D2PEntry> {
    override fun parse(raw: ByteBuffer, params: D2PEntry) =
        D2PData(params.key, raw.readBytes(params.size))
}