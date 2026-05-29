package services

import java.awt.image.BufferedImage

interface ParamsRenderService<T : Any, P : Any> {
    fun render(raw: BufferedImage, params: P): T
}