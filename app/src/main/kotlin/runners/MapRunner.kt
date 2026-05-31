package bouftou.app.runners

import adapter.services.WorldAdapterService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import d2p.services.D2PService
import dlm.services.MapService
import ele.services.ElementsService
import entities.BlopEntry
import services.BlopService
import services.GraphicService
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO

class MapRunner(
    private val d2pService: D2PService,
    private val elementsService: ElementsService,
    private val mapService: MapService,
    private val worldAdapterService: WorldAdapterService,
    private val blopService: BlopService,
    private val graphicService: GraphicService
) : CliktCommand("map") {
    val inputOption by option("--input", "-i", help = "Input folder path").required()
    val outputOption by option("--output", "-o", help = "Output folder path").required()
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
        println(
            """
            neighbours:
                - top: ${map.topNeighbourId}
                - right: ${map.rightNeighbourId}
                - bottom: ${map.bottomNeighbourId}
                - left: ${map.leftNeighbourId}
        """.trimIndent()
        )
        ImageIO.write(
            blopService.render(
                graphicService.createCanvas(1280, 1024), BlopEntry(
                    d2pEntry, elements, map,
                    worldAdapterService.pngLoader(d2pEntry, d2pService::parseDataFromEntry),
                    worldAdapterService.jpgLoader(d2pEntry, d2pService::parseDataFromEntry)
                )
            ),
            "png",
            File(
                Paths.get(outputOption)
                    .resolve("${map.id}.png")
                    .toAbsolutePath()
                    .toString()
            )
        )
    }
}