package bouftou.app.modules

import bouftou.app.runners.AppRunner
import org.koin.dsl.module

val appModule = module {
    // runner
    single { AppRunner() }
}