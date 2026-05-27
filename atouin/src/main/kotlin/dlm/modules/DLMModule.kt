package dlm.modules

import dlm.services.*
import dlm.services.elements.BasicElementService
import dlm.services.elements.GraphicalElementService
import dlm.services.elements.SoundElementService
import org.koin.dsl.module

val dlmModule = module {
    single { MapService(get(), get(), get()) }
    single { LayerService(get()) }
    single { CellService(get()) }
    single { FixtureService() }
    single { CellDataService(get(), get()) }
    single { CellDataV9Service() }
    single { CellDataLegacyService() }

    single { BasicElementService(get(), get()) }
    single { GraphicalElementService() }
    single { SoundElementService() }
}