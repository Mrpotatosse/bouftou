package bouftou.app

import bouftou.app.modules.appModule
import bouftou.app.runners.AppRunner
import com.github.ajalt.clikt.core.main
import d2p.modules.d2pModule
import dlm.modules.dlmModule
import ele.modules.eleModule
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get

fun main(vararg args: String) {
    startKoin {
        modules(
            appModule,
            d2pModule,
            eleModule,
            dlmModule,
        )
    }

    get<AppRunner>(AppRunner::class.java).main(args)
}