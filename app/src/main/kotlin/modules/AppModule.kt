package bouftou.app.modules

import bouftou.app.runners.AppRunner
import bouftou.app.runners.D2PRunner
import bouftou.app.runners.EleRunner
import bouftou.app.runners.MapRunner
import com.github.ajalt.clikt.core.subcommands
import org.koin.dsl.module

val appModule = module {
    // runner
    single {
        AppRunner()
            .subcommands(
                get<D2PRunner>(),
                get<EleRunner>(),
                get<MapRunner>(),
            )
    }
    single { D2PRunner(get()) }
    single { EleRunner(get()) }
    single { MapRunner(get(), get()) }
}