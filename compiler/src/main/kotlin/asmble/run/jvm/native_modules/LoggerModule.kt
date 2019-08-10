package asmble.run.jvm.native_modules

import asmble.compile.jvm.Mem
import java.io.PrintWriter
import java.nio.ByteBuffer

/**
 * Module used for logging UTF-8 strings from a Wasm module to a given writer.
 */
open class LoggerModule(val writer: PrintWriter) {

    // one memory page is quite enough for save temporary buffer
    private val memoryPages = 1

    private val memory =
            ByteBuffer.allocate(memoryPages * Mem.PAGE_SIZE) as ByteBuffer

    /**
     * [Wasm function]
     * Writes one byte to the logger memory buffer. If there is no place flushes
     * all data from the buffer to [PrintWriter] and try to put the byte again.
     */
    fun write(byte: Int) {
        val isFull = memory.position() >= memory.limit()
        if (isFull) {
            flush()
        }
        memory.put(byte.toByte())
    }


    /**
     * [Wasm function]
     * Reads all bytes from the logger memory buffer, convert its to UTF-8
     * string and writes to stdout.
     * Cleans the logger memory buffer.
     */
    fun flush() {
        val message = String(memory.array(), 0, memory.position())
        writer.print(message)
        writer.flush()
        memory.clear()
    }

}
