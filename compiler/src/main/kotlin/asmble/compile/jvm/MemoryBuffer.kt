package asmble.compile.jvm

import java.nio.ByteBuffer
import java.nio.ByteOrder

interface MemoryBuffer {
    fun capacity(): Int
    fun limit(): Int
    fun limit(newLimit: Int): MemoryBuffer
    fun position(newPosition: Int): MemoryBuffer
    fun order(order: ByteOrder): MemoryBuffer
    fun duplicate(order: ByteOrder): MemoryBuffer
    fun put(arr: ByteArray, offset: Int, length: Int): MemoryBuffer
    fun getInt(index: Int): Int
    fun get(index: Int): Byte
    fun getLong(index: Int): Long
    fun getShort(index: Int): Short
    fun getFloat(index: Int): Float
    fun getDouble(index: Int): Double

}

class MemoryByteBuffer(val bb: ByteBuffer) : MemoryBuffer {
    override fun capacity(): Int {
        return bb.capacity()
    }

    override fun limit(): Int {
        return bb.limit()
    }

    override fun limit(newLimit: Int): MemoryBuffer {
        bb.limit(newLimit)
        return this
    }

    override fun position(newPosition: Int): MemoryBuffer {
        bb.position(newPosition)
        return this
    }

    override fun order(order: ByteOrder): MemoryBuffer {
        bb.order(order)
        return this
    }

    override fun duplicate(order: ByteOrder): MemoryBuffer {
        return MemoryByteBuffer(bb.duplicate())
    }

    override fun put(arr: ByteArray, offset: Int, length: Int): MemoryBuffer {
        bb.put(arr, offset, length)
        return this
    }

    override fun getInt(index: Int): Int {
        return bb.getInt(index)
    }

    override fun get(index: Int): Byte {
        return bb.get(index)
    }

    override fun getLong(index: Int): Long {
        return bb.getLong(index)
    }

    override fun getShort(index: Int): Short {
        return bb.getShort(index)
    }

    override fun getFloat(index: Int): Float {
        return bb.getFloat(index)
    }

    override fun getDouble(index: Int): Double {
        return bb.getDouble(index)
    }

}

interface MemoryBufferInitializator {
    val memTypeRef: TypeRef
    fun init(capacity: Int): MemoryBuffer
}

class MemoryByteBufferInitializator(val direct: Boolean) : MemoryBufferInitializator {
    override val memTypeRef: TypeRef = MemoryByteBuffer::class.ref
    override fun init(capacity: Int): MemoryBuffer {
        println("init: " + memTypeRef)
        return if (direct) MemoryByteBuffer(ByteBuffer.allocateDirect(capacity))
        else MemoryByteBuffer(ByteBuffer.allocate(capacity))
    }

}