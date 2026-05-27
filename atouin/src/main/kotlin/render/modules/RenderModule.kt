package render.modules

import org.koin.dsl.module
import render.services.RenderService

val renderModule = module {
    single { RenderService() }
}