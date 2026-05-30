package modules

import org.koin.dsl.module
import services.ChaferService

val chaferModule =
    module {
        includes(blopModule, hiboukinModule)
        single {
            ChaferService(
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }