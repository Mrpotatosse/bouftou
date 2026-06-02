package stores

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.io.File
import java.io.RandomAccessFile
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.time.Duration

class OffHeapReader<K : Any, V : Any>(file: File, private val index: Map<K, Long>) {
    private val kryo = Kryo().apply { isRegistrationRequired = false }
    private val channel = RandomAccessFile(file, "r").channel
    private val mapped: MappedByteBuffer =
        channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size())

    private val cache: LoadingCache<K, V?> = Caffeine.newBuilder()
        .maximumSize(1024 * 1024)
        .expireAfterWrite(Duration.ofSeconds(30))
        .build(::internalGet)

    @Suppress("UNCHECKED_CAST")
    private fun internalGet(key: K): V? {
        val offset = index[key] ?: return null
        val slice = mapped.duplicate() // thread-safe: each caller gets its own position
        slice.position(offset.toInt())

        val length = slice.int  // reads 4 bytes
        val bytes = ByteArray(length)
        slice.get(bytes)

        return kryo.readClassAndObject(Input(bytes)) as V?
    }

    fun get(key: K) = cache.get(key)
}