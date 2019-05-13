package asmble.compile.jvm

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * The default implementation of MemoryBuffer that based on java.nio.ByteBuffer
 */
open class MemoryByteBuffer(val bb: ByteBuffer) : MemoryBuffer() {
    override fun put(arr: ByteArray): MemoryBuffer {
        bb.put(arr)
        return this
    }

    override fun clear(): MemoryBuffer {
        bb.clear()
        return this
    }

    override fun get(arr: ByteArray): MemoryBuffer {
        bb.get(arr)
        return this
    }

    override fun putLong(index: Int, n: Long): MemoryBuffer {
        bb.putLong(index, n)
        return this
    }

    override fun putDouble(index: Int, n: Double): MemoryBuffer {
        bb.putDouble(index, n)
        return this
    }

    override fun putShort(index: Int, n: Short): MemoryBuffer {
        bb.putShort(index, n)
        return this
    }

    override fun putFloat(index: Int, n: Float): MemoryBuffer {
        bb.putFloat(index, n)
        return this
    }

    override fun put(index: Int, b: Byte): MemoryBuffer {
        bb.put(index, b)
        return this
    }

    override fun putInt(index: Int, n: Int): MemoryBuffer {
        bb.putInt(index, n)
        return this
    }

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

    override fun duplicate(): MemoryBuffer {
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

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other !is MemoryByteBuffer)
            return false
        return bb == other.bb
    }

    override fun hashCode(): Int {
        return bb.hashCode()
    }
}
