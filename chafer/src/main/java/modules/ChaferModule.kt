package modules

import kit.LoopService
import kit.WindowService
import layouts.Main
import org.koin.dsl.module
import services.ChaferService
import services.UIService
import services.components.MapComponentService

val chaferModule =
    module {
        includes(blopModule, hiboukinModule)
        single {
            UIService(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }

        single { Main(get()) }
        single { MapComponentService() }

        single { ChaferService() }
        single { WindowService() }
        single { LoopService() }
    }