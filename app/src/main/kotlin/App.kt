package bouftou.app

import bouftou.app.modules.appModule
import bouftou.app.runners.AppRunner
import com.github.ajalt.clikt.core.main
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get

fun main(args: Array<String>) {
    startKoin {
        modules(
            appModule
        )
    }

    val runner = get<AppRunner>(AppRunner::class.java)
    runner.main(args)
}