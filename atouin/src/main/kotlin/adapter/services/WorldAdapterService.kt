package adapter.services

import d2p.entitites.D2PData
import d2p.entitites.D2PEntry
import extensions.asInputStream
import extensions.deflate
import stores.OffHeapReader
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import javax.imageio.ImageIO

class WorldAdapterService {
    fun parseDlm(
        d2p: Map<String, D2PEntry>,
        id: Int,
        parser: (entry: D2PEntry) -> D2PData
    ) = "content/maps/${id % 10}/$id.dlm".let {
        if (d2p.containsKey(it))
            ByteBuffer.wrap(parser.invoke(d2p[it]!!).buffer).deflate()
        else null
    }

    fun parseDlm(
        d2p: OffHeapReader<String, D2PEntry>,
        id: Int,
        parser: (entry: D2PEntry) -> D2PData
    ) = "content/maps/${id % 10}/$id.dlm".let {
        val data = d2p.get(it)
        if (data != null)
            ByteBuffer.wrap(parser.invoke(data).buffer).deflate()
        else null
    }

    fun parsePng(
        d2p: Map<String, D2PEntry>,
        id: Int,
        parser: (entry: D2PEntry) -> D2PData
    ): ByteBuffer? = "content/gfx/world/png/$id.png".let {
        if (d2p.containsKey(it))
            ByteBuffer.wrap(parser.invoke(d2p[it]!!).buffer)
        else null
    }

    fun parsePng(
        d2p: OffHeapReader<String, D2PEntry>,
        id: Int,
        parser: (entry: D2PEntry) -> D2PData
    ): ByteBuffer? = "content/gfx/world/png/$id.png".let {
        val data = d2p.get(it)
        if (data != null)
            ByteBuffer.wrap(parser.invoke(data).buffer)
        else null
    }

    fun parseJpg(
        d2p: Map<String, D2PEntry>,
        id: Int,
        parser: (entry: D2PEntry) -> D2PData
    ): ByteBuffer? = "content/gfx/world/jpg/$id.jpg".let {
        if (d2p.containsKey(it))
            ByteBuffer.wrap(parser.invoke(d2p[it]!!).buffer)
        else null
    }

    fun parseJpg(
        d2p: OffHeapReader<String, D2PEntry>,
        id: Int,
        parser: (entry: D2PEntry) -> D2PData
    ): ByteBuffer? = "content/gfx/world/jpg/$id.jpg".let {
        val data = d2p.get(it)
        if (data != null)
            ByteBuffer.wrap(parser.invoke(data).buffer)
        else null
    }

    fun pngLoader(
        d2p: Map<String, D2PEntry>,
        parser: (entry: D2PEntry) -> D2PData
    ): (Int) -> BufferedImage? = { id ->
        val png = parsePng(d2p, id, parser)
        if (png != null) ImageIO.read(png.asInputStream())
        else null
    }

    fun pngLoader(
        d2p: OffHeapReader<String, D2PEntry>,
        parser: (entry: D2PEntry) -> D2PData
    ): (Int) -> BufferedImage? = { id ->
        val png = parsePng(d2p, id, parser)
        if (png != null) ImageIO.read(png.asInputStream())
        else null
    }

    fun jpgLoader(
        d2p: Map<String, D2PEntry>,
        parser: (entry: D2PEntry) -> D2PData
    ): (Int) -> BufferedImage? = { id ->
        val jpg = parseJpg(d2p, id, parser)
        if (jpg != null) ImageIO.read(jpg.asInputStream())
        else null
    }

    fun jpgLoader(
        d2p: OffHeapReader<String, D2PEntry>,
        parser: (entry: D2PEntry) -> D2PData
    ): (Int) -> BufferedImage? = { id ->
        val jpg = parseJpg(d2p, id, parser)
        if (jpg != null) ImageIO.read(jpg.asInputStream())
        else null
    }
}