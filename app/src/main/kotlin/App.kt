package bouftou.app

import bouftou.app.modules.appModule
import bouftou.app.runners.AppRunner
import com.github.ajalt.clikt.core.main
import modules.atouinModule
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get

fun main(vararg args: String) {
    startKoin {
        modules(
            appModule,
            atouinModule
        )
    }

    get<AppRunner>(AppRunner::class.java).main(args)
}