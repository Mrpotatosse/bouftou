package d2o.modules

import d2o.services.*
import org.koin.dsl.module

val d2oModule = module {
    single { D2OService(get(), get()) }
    single { D2OEntryService(get(), get()) }
    single { D2OEntryIndexService() }
    single { D2OEntryClassService(get()) }
    single { D2OEntryFieldService() }
    single { D2OObjectService(get()) }
    single { D2OObjectFieldsService() }
}