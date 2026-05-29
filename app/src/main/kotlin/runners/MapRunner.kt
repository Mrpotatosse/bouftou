package bouftou.app.runners

import adapter.services.WorldAdapterService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import d2p.services.D2PService
import dlm.services.MapService
import ele.services.ElementsService
import java.nio.file.Paths

class MapRunner(
    private val d2pService: D2PService,
    private val elementsService: ElementsService,
    private val mapService: MapService,
    private val worldAdapterService: WorldAdapterService,
) : CliktCommand("map") {
    val inputOption by option("--input", "-i", help = "Input folder path").required()
    val mapIdOption by option("--map-id", "-m", help = "Map id").int().required()

    override fun run() {
        val d2pEntry = d2pService.parseEntryFromFolder(inputOption)
        val elements = elementsService.parseElementsFromFile(
            Paths.get(inputOption)
                .resolve("content")
                .resolve("maps")
                .resolve("elements.ele")
        )
        val map = mapService.parse(
            worldAdapterService.parseDlm(d2pEntry, mapIdOption, d2pService::parseDataFromEntry)
                ?: error("Map buffer could not be parsed")
        )

        println(map)
        println(elements)
    }
}