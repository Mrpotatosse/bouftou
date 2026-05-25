package bouftou.app.runners

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import d2p.services.D2PService
import dlm.services.MapService
import extensions.deflate
import java.nio.ByteBuffer

class MapRunner(
    private val d2pService: D2PService,
    private val mapService: MapService
) : CliktCommand("map") {
    val inputOption by option("--input", "-i", help = "Input folder path").required()
    val mapId by option("--map-id", "-m", help = "Map id").int().required()

    override fun run() {
        val mapDlm = "${mapId % 10}/$mapId.dlm"
        val d2pEntry = d2pService.parseEntryFromFolder(inputOption)
        val d2pData = d2pService.parseDataFromEntry(d2pEntry[mapDlm]!!)
        val map = mapService.parse(ByteBuffer.wrap(d2pData.buffer).deflate())
        println(map)
    }
}