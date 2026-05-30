package bouftou.app.runners

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import services.ChaferService

class UIRunner(
    private val chaferService: ChaferService,
) : CliktCommand("ui") {
    val inputOption by option("--input", "-i", help = "Input folder path").required()
    override fun run() {
        chaferService.application(inputOption)
    }
}