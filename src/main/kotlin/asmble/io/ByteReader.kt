package asmble.io

import asmble.util.toIntExact
import asmble.util.toUnsignedBigInt
import asmble.util.toUnsignedLong
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.math.BigInteger

abstract class ByteReader {
    abstract val isEof: Boolean

    // Slices the next set off as its own and moves the position up that much
    abstract fun read(amount: Int): ByteReader
    abstract fun readByte(): Byte
    abstract fun readBytes(amount: Int? = null): ByteArray

    fun readUInt32(): Long {
        return ((readByte().toInt() and 0xff) or
            ((readByte().toInt() and 0xff) shl 8) or
            ((readByte().toInt() and 0xff) shl 16) or
            ((readByte().toInt() and 0xff) shl 24)).toUnsignedLong()
    }

    fun readUInt64(): BigInteger {
        return ((readByte().toLong() and 0xff) or
            ((readByte().toLong() and 0xff) shl 8) or
            ((readByte().toLong() and 0xff) shl 16) or
            ((readByte().toLong() and 0xff) shl 24) or
            ((readByte().toLong() and 0xff) shl 32) or
            ((readByte().toLong() and 0xff) shl 40) or
            ((readByte().toLong() and 0xff) shl 48) or
            ((readByte().toLong() and 0xff) shl 56)).toUnsignedBigInt()
    }

    fun readVarInt7() = readSignedLeb128().let {
        require(it >= Byte.MIN_VALUE.toLong() && it <= Byte.MAX_VALUE.toLong())
        it.toByte()
    }

    fun readVarInt32() = readSignedLeb128().toIntExact()

    fun readVarInt64() = readSignedLeb128()

    fun readVarUInt1() = readUnsignedLeb128().let {
        require(it == 1 || it == 0)
        it == 1
    }

    fun readVarUInt7() = readUnsignedLeb128().let {
        require(it <= 255)
        it.toShort()
    }

    fun readVarUInt32() = readUnsignedLeb128().toUnsignedLong()

    protected fun readUnsignedLeb128(): Int {
        // Taken from Android source, Apache licensed
        var result = 0
        var cur = 0
        var count = 0
        do {
            cur = readByte().toInt() and 0xff
            result = result or ((cur and 0x7f) shl (count * 7))
            count++
        } while (cur and 0x80 == 0x80 && count < 5)
        if (cur and 0x80 == 0x80) throw NumberFormatException()
        return result
    }

    private fun readSignedLeb128(): Long {
        // Taken from Android source, Apache licensed
        var result = 0L
        var cur = 0
        var count = 0
        var signBits = -1L
        do {
            cur = readByte().toInt() and 0xff
            result = result or ((cur and 0x7f).toLong() shl (count * 7))
            signBits = signBits shl 7
            count++
        } while (cur and 0x80 == 0x80 && count < 10)
        if (cur and 0x80 == 0x80) throw NumberFormatException()
        if ((signBits shr 1) and result != 0L) result = result or signBits
        return result
    }

    class InputStream(val ins: java.io.InputStream) : ByteReader() {
        private var nextByte: Byte? = null
        private var sawEof = false
        override val isEof: Boolean get() {
            if (!sawEof && nextByte == null) {
                val b = ins.read()
                if (b == -1) sawEof = true else nextByte = b.toByte()
            }
            return sawEof && nextByte == null
        }

        override fun read(amount: Int) = ByteReader.InputStream(ByteArrayInputStream(readBytes(amount)))

        override fun readByte(): Byte {
            nextByte?.let { nextByte = null; return it }
            val b = ins.read()
            if (b >= 0) return b.toByte()
            sawEof = true
            throw IoErr.UnexpectedEnd()
        }

        override fun readBytes(amount: Int?): ByteArray {
            // If we have the amount, we create a byte array for reading that
            // otherwise we read until the end
            if (amount != null) {
                val ret = ByteArray(amount)
                var remaining = amount
                if (nextByte != null) {
                    ret[0] = nextByte!!
                    nextByte = null
                    remaining--
                }
                while (remaining > 0) {
                    val off = amount - remaining
                    val read = ins.read(ret, off, remaining)
                    if (read == -1) {
                        sawEof = true
                        throw IoErr.UnexpectedEnd()
                    }
                    remaining -= read
                }
                return ret
            } else {
                val out = ByteArrayOutputStream()
                if (nextByte != null) {
                    out.write(nextByte!!.toInt())
                    nextByte = null
                }
                val buf = ByteArray(8192)
                while (true) {
                    val r = ins.read(buf)
                    if (r == -1) break
                    out.write(buf, 0, r)
                }
                sawEof = true
                return out.toByteArray()
            }
        }
    }
}