package d2p.modules

import d2p.services.D2PDataService
import d2p.services.D2PEntryService
import d2p.services.D2PService
import org.koin.dsl.module

val d2pModule = module {
    single { D2PService(get(), get()) }
    single { D2PDataService() }
    single { D2PEntryService() }
}