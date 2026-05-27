package bouftou.app.runners

import adapter.services.WorldAdapterService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import d2p.services.D2PService
import dlm.services.MapService
import ele.services.ElementsService
import render.services.RenderService
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class MapRunner(
    private val d2pService: D2PService,
    private val elementsService: ElementsService,
    private val mapService: MapService,
    private val renderService: RenderService,
    private val worldAdapterService: WorldAdapterService
) : CliktCommand("map") {
    val inputOption by option("--input", "-i", help = "Input folder path").required()
    val outputOption by option("--output", "-o", help = "Output folder").required()
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
            worldAdapterService.parseDlm(d2pEntry, mapIdOption) ?: error("Map buffer could not be parsed")
        )
        val outputDir = Paths.get(outputOption)
        Files.createDirectories(outputDir)
        val outputFile = outputDir.resolve("$mapIdOption.png")
        Files.write(
            outputFile,
            renderService.drawMap(
                map,
                worldAdapterService.jpgLoader(d2pEntry),
                worldAdapterService.pngLoader(d2pEntry),
                elements
            ),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        )
    }
}