package adapter.modules

import adapter.services.WorldAdapterService
import org.koin.dsl.module

val adapterModule = module {
    single { WorldAdapterService() }
}