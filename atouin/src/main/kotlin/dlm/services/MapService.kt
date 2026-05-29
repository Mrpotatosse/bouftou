package dlm.services

import const.AtouinConstants
import dlm.entities.*
import extensions.*
import services.ParserService
import java.nio.ByteBuffer

class MapService(
    private val fixtureService: FixtureService,
    private val layerService: LayerService,
    private val cellDataService: CellDataService
) : ParserService<DofusMap> {
    val decryptionKey = AtouinConstants.MAP_KEY.toByteArray(Charsets.UTF_8)

    override fun parse(raw: ByteBuffer): DofusMap {
        if (raw.readByte().toInt() != 77) throw IllegalArgumentException("Invalid Map")
        val result = DofusMap(
            raw.readByte(),
            raw.readUnsignedInt()
        )

        val decryptedRaw: ByteBuffer = if (result.mapVersion >= 7) {
            result.encrypted = raw.readBoolean()
            result.encryptionVersion = raw.readByte()
            val dataLen = raw.readInt()
            if (result.encrypted) {
                val enc = raw.readBytes(dataLen)
                for (i in enc.indices)
                    enc[i] = (enc[i].toInt() xor decryptionKey[i % decryptionKey.size].toInt()).toByte()
                ByteBuffer.wrap(enc)
            } else {
                raw
            }
        } else {
            raw
        }

        // ── Core identity ────────────────────────────────────────────────
        result.relativeId = decryptedRaw.readUnsignedInt()
        result.mapType = MapType.fromId(decryptedRaw.readByte())
        result.subareaId = decryptedRaw.readInt()


        result.topNeighbourId = decryptedRaw.readInt()
        result.bottomNeighbourId = decryptedRaw.readInt()
        result.leftNeighbourId = decryptedRaw.readInt()
        result.rightNeighbourId = decryptedRaw.readInt()
        result.shadowBonusOnEntities = decryptedRaw.readUnsignedInt()

        // ── Colours ──────────────────────────────────────────────────────
        if (result.mapVersion >= 9) {
            var c = decryptedRaw.readInt()
            // TODO: before:(c shr 24) and 0xFF after:(c shr 24).toByte()
            result.backgroundAlpha = (c shr 24) and 0xFF
            result.backgroundRed = (c shr 16) and 0xFF
            result.backgroundGreen = (c shr 8) and 0xFF
            result.backgroundBlue = c and 0xFF
            c = decryptedRaw.readInt()
            val gA = (c shr 24) and 0xFF
            val gR = (c shr 16) and 0xFF
            val gG = (c shr 8) and 0xFF
            val gB = c and 0xFF
            result.gridColor = packArgb(gA, gR, gG, gB)
        } else if (result.mapVersion >= 3) {
            result.backgroundRed = decryptedRaw.readByte().toInt()
            result.backgroundGreen = decryptedRaw.readByte().toInt()
            result.backgroundBlue = decryptedRaw.readByte().toInt()
        }

        if (result.mapVersion >= 4) {
            result.zoomScale = decryptedRaw.readUnsignedShort().toDouble() / 100.0
            result.zoomOffsetX = decryptedRaw.readShort()
            result.zoomOffsetY = decryptedRaw.readShort()
            if (result.zoomScale < 1.0) {
                result.zoomScale = 1.0; result.zoomOffsetX = 0; result.zoomOffsetY = 0
            }
        }

        // ── Audio ────────────────────────────────────────────────────────
        result.useLowPassFilter = decryptedRaw.readBoolean()
        result.useReverb = decryptedRaw.readBoolean()
        result.presetId = if (result.useReverb) decryptedRaw.readInt() else -1

        // ── Background fixtures ──────────────────────────────────────────
        result.backgroundsCount = decryptedRaw.readByte()
        repeat(result.backgroundsCount.toInt()) {
            result.backgroundFixtures.add(
                fixtureService.parse(
                    decryptedRaw,
                    Fixture(result)
                )
            )
        }

        // ── Foreground fixtures ──────────────────────────────────────────
        result.foregroundsCount = decryptedRaw.readByte()
        repeat(result.foregroundsCount.toInt()) {
            result.foregroundFixtures.add(
                fixtureService.parse(
                    decryptedRaw,
                    Fixture(result)
                )
            )
        }

        // ── Ground layer ─────────────────────────────────────────────────
        result.cellsCount = AtouinConstants.MAP_CELLS_COUNT
        decryptedRaw.readInt()   // reserved, ignored
        result.groundCRC = decryptedRaw.readInt()
        // ── Layers ───────────────────────────────────────────────────────
        result.layersCount = decryptedRaw.readByte()
        repeat(result.layersCount.toInt()) {
            result.layers.add(
                layerService.parse(
                    decryptedRaw,
                    Layer(result)
                )
            )
        }

        // ── Cell data ────────────────────────────────────────────────────
        var oldMvtSystem: Byte = 0
        repeat(result.cellsCount) { i ->
            val cd = cellDataService.parse(decryptedRaw, CellData(result, i))
            if (oldMvtSystem in 0..0) oldMvtSystem = cd.moveZone
            if (cd.moveZone != oldMvtSystem) result.isUsingNewMovementSystem = true
            result.cells.add(cd)
        }

        return result
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private fun packArgb(a: Int, r: Int, g: Int, b: Int): Long =
        ((a.toLong() and 0xFF) shl 24) or
                ((r.toLong() and 0xFF) shl 16) or
                ((g.toLong() and 0xFF) shl 8) or
                (b.toLong() and 0xFF)
}