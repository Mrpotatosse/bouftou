package services

import java.nio.ByteBuffer

interface ParserService<T : Any> {
    fun parse(raw: ByteBuffer): T
}