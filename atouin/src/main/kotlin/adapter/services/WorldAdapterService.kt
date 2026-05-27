package adapter.services

import d2p.entitites.D2PEntry
import d2p.services.D2PService
import extensions.asInputStream
import extensions.deflate
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import javax.imageio.ImageIO

class WorldAdapterService(
    private val d2pService: D2PService,
) {
    fun parseDlm(
        d2p: Map<String, D2PEntry>,
        id: Int
    ) = "content/maps/${id % 10}/$id.dlm".let {
        if (d2p.containsKey(it))
            ByteBuffer.wrap(d2pService.parseDataFromEntry(d2p[it]!!).buffer).deflate()
        else null
    }

    fun parsePng(
        d2p: Map<String, D2PEntry>,
        id: Int
    ): ByteBuffer? = "content/gfx/world/png/$id.png".let {
        if (d2p.containsKey(it))
            ByteBuffer.wrap(d2pService.parseDataFromEntry(d2p[it]!!).buffer)
        else null
    }

    fun parseJpg(
        d2p: Map<String, D2PEntry>,
        id: Int
    ): ByteBuffer? = "content/gfx/world/jpg/$id.jpg".let {
        if (d2p.containsKey(it))
            ByteBuffer.wrap(d2pService.parseDataFromEntry(d2p[it]!!).buffer)
        else null
    }

    fun pngLoader(d2p: Map<String, D2PEntry>): (Int) -> BufferedImage? = { id ->
        val png = parsePng(d2p, id)
        if (png != null) ImageIO.read(png.asInputStream())
        else null
    }

    fun jpgLoader(d2p: Map<String, D2PEntry>): (Int) -> BufferedImage? = { id ->
        val jpg = parseJpg(d2p, id)
        if (jpg != null) ImageIO.read(jpg.asInputStream())
        else null
    }
}