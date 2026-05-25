package bouftou.app.runners

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import ele.services.ElementsService

class EleRunner(
    private val elementsService: ElementsService,
) : CliktCommand("ele") {
    val inputOption by option("--input", "-i", help = "Input file name").required()

    override fun run() {
        println(elementsService.parseElementsFromFile(inputOption))
    }
}