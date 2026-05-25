package ele.modules

import ele.services.*
import ele.services.subtypes.*
import org.koin.dsl.module

val eleModule = module {
    single { ElementsService(get(), get(), get()) }
    single { ElementEntryService() }
    single { ElementDataService(get()) }
    single { ElementIsJpgService() }

    single {
        GraphicalElementDataService(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single { NormalGraphicalElementDataService() }
    single { AnimatedGraphicalElementDataService(get()) }
    single { BlendedGraphicalElementDataService(get()) }
    single { BoundingBoxGraphicalElementDataService(get()) }
    single { EntityGraphicalElementDataService() }
    single { ParticlesGraphicalElementDataService() }
}