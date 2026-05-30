package d2o.modules

import d2o.services.D2OEntryService
import d2o.services.D2OService
import org.koin.dsl.module

val d2oModule = module {
    single { D2OService(get()) }
    single { D2OEntryService() }
}