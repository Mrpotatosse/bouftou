package stores

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import java.io.File
import java.nio.ByteBuffer

class OffHeapStore<K : Any, V : Any>(private val file: File) {
    private val index = HashMap<K, Long>()
    private val output = file.outputStream().buffered()
    private val kryo = Kryo().apply {
        isRegistrationRequired = false
    }
    private var offset = 0L

    fun put(key: K, value: V) {
        val buf = Output(4096, -1)
        kryo.writeClassAndObject(buf, value)
        val bytes = buf.toBytes()

        // Write [4-byte length][payload]
        output.write(ByteBuffer.allocate(4).putInt(bytes.size).array())
        output.write(bytes)

        index[key] = offset
        offset += 4 + bytes.size
    }

    fun putAll(map: Map<K, V>) = map.forEach { (k, v) -> put(k, v) }

    fun seal(): OffHeapReader<K, V> {
        output.flush()
        output.close()
        return OffHeapReader(file, index)
    }
}