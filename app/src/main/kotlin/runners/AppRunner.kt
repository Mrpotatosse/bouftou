package bouftou.app.runners

import com.github.ajalt.clikt.core.CliktCommand

class AppRunner : CliktCommand() {
    override fun run() {
        println("Hello World !!!")
    }
}