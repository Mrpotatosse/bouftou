package d2p.services

import d2p.entitites.D2PEntry
import extensions.*
import services.ParamsParserService
import java.nio.ByteBuffer
import java.nio.file.Paths

class D2PEntryService : ParamsParserService<Map<String, D2PEntry>, Pair<String, String>> {
    override fun parse(raw: ByteBuffer, params: Pair<String, String>): Map<String, D2PEntry> =
        mutableMapOf<String, D2PEntry>().let { result ->
            val num = (raw.readByte() + raw.readByte()).toByte()
            if (num.toInt() != 3) return result
            raw.seek(raw.size() - 24)

            val num2 = raw.readInt()
            raw.readInt() // ignored
            val num3 = raw.readInt()
            val num4 = raw.readInt()
            raw.readInt()
            raw.readInt()

            raw.seek(num3)
            repeat(num4) {
                val key = raw.readUTF()
                val offset = raw.readInt() + num2
                val size = raw.readInt()
                val newKey = Paths.get(params.second)
                    .relativize(Paths.get(params.first).parent)
                    .resolve(key)
                    .toString()
                result[newKey] = D2PEntry(
                    offset,
                    size,
                    newKey,
                    params.first
                )
            }

            result
        }
}