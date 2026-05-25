package services

import java.nio.ByteBuffer

interface ParamsParserService<T : Any, P : Any> {
    fun parse(raw: ByteBuffer, params: P): T
}