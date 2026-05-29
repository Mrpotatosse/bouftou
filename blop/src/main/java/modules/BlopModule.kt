package modules

import org.koin.dsl.module
import services.*

val blopModule =
    module {
        includes(atouinModule)
        single { GraphicService() }
        single { BlopService(get(), get(), get(), get()) }
        single { BackgroundRenderService(get()) }
        single { LayerRenderService(get()) }
        single { ForegroundRenderService(get()) }
        single { FixtureRenderService(get()) }
    }
