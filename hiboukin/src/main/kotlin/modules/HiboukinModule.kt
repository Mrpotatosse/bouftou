package modules

import adapter.modules.adapterModule
import d2o.modules.d2oModule
import org.koin.dsl.module

val hiboukinModule = module {
    includes(
        d2oModule,
        adapterModule
    )
}