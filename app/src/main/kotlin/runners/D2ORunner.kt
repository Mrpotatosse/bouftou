package bouftou.app.runners

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import d2o.entities.D2ODataType
import d2o.services.D2OService

class D2ORunner(
    private val d2oService: D2OService,
) : CliktCommand("d2o") {
    val inputOption by option("--input", "-i", help = "Input folder path").required()

    override fun run() {
        val d2oEntry = d2oService.parseEntryFromFolder(inputOption)[D2ODataType.MapScrollActions]!!
        val obj = d2oService.parseObjectFromEntry(d2oEntry, 0)
        println(obj?.fields?.getAsBoolean("rightExists"))
    }
}