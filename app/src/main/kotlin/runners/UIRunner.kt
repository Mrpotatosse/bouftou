package bouftou.app.runners

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kit.LoopService
import kit.WindowService

class UIRunner(
    private val loopService: LoopService,
    private val windowService: WindowService
) : CliktCommand("ui") {
    val inputOption by option("--input", "-i", help = "Input folder path").required()
    override fun run() {
        windowService.open()
    }
}