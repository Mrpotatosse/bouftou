package services

import java.nio.ByteBuffer

interface RenderService<T : Any> {
    fun render(raw: ByteBuffer): T
}