package bouftou.app.runners

import com.github.ajalt.clikt.core.CliktCommand

class EleRunner : CliktCommand("ele") {
    override fun run() {
        println("Hello ele")
    }
}