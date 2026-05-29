package modules

import adapter.modules.adapterModule
import d2p.modules.d2pModule
import dlm.modules.dlmModule
import ele.modules.eleModule
import org.koin.dsl.module

val atouinModule = module {
    includes(
        d2pModule,
        eleModule,
        dlmModule,
        // renderModule,
        adapterModule
    )
}