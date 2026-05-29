package bouftou.app.modules

import bouftou.app.runners.*
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
                get<BlopRunner>(),
            )
    }
    single { D2PRunner(get()) }
    single { EleRunner(get()) }
    single {
        MapRunner(
            get(),
            get(),
            get(),
            get()
        )
    }
    single {
        BlopRunner(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}