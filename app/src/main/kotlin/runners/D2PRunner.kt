package bouftou.app.runners

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import d2p.services.D2PService

class D2PRunner(
    private val d2pService: D2PService,
) : CliktCommand("d2p") {
    val inputOption by option("--input", "-i", help = "Input folder path").required()

    override fun run() {
        println(d2pService.parseEntryFromFolder(inputOption))
    }
}